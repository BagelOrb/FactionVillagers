package characters;

import java.util.LinkedList;
import java.util.List;

import main.Debug;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import utils.BlockChecker;
import utils.BlockUtils;
import utils.WalkingGroundFinder;
import characters.Character.Action;
import characters.Character.ActionContainer;
import characters.Character.ActionType;

public class Navigator {

	LinkedList<GoAndDo> wayPoints ;
//	private int wayPointNext = 0;
//	public boolean destinationReached = false;
	
	public Navigator() {
		wayPoints = new LinkedList<GoAndDo>();
	}
	
	public Navigator(List<GoAndDo> wayPoints2) {
		this();
		this.wayPoints = new LinkedList<GoAndDo>(wayPoints2);
	}
	public static abstract class GoAndDo {
		public final Location loc;
		public Location getLocation() {
			return loc;
		}

		/**
		 * @param npc
		 * @return whether the action is completed (and no further navigation instructions are given to the npc!)
		 */
		public abstract boolean doAction(Character npc);
		
		public GoAndDo(Location l) { loc = l; }
		
		
		public static GoAndDo newNoAction(Location l) {
			return new GoAndDo(l) {
				@Override
				public boolean doAction(Character npc) {
					return true;
				}

				@Override
				public ActionType getActionType() {
					return ActionType.MOVE_THROUGH;
				}
			};
		}
		public String toString() {
			return "loc: "+loc;
		}

		public abstract ActionType getActionType() ;
	}
	
	
	public static class GoAndOpenGate extends GoAndDo {

		private Block gate;

		public GoAndOpenGate(Location l, Block gate) {
			super(l);
			this.gate = gate;
		}

		@Override
		public boolean doAction(Character npc) {
//			Debug.out("gate is open: "+BlockUtils.isOpenGate(gate));
			BlockUtils.setGateOpen(gate, true);
			BlockUtils.setGateOpen(gate.getRelative(BlockFace.UP), true);
			return true;
		}
		
		@Override public String toString() {
			return "loc: "+loc+"OpenGate";
		}
		@Override
		public ActionType getActionType() {
			return ActionType.OPEN_GATE;
		}
		
	}
	public static class GoAndCloseGate extends GoAndDo {
		
		private Block gate;
		
		public GoAndCloseGate(Location l, Block gate) {
			super(l);
			this.gate = gate;
		}
		
		@Override
		public boolean doAction(Character npc) {
			BlockUtils.setGateOpen(gate, false);
			BlockUtils.setGateOpen(gate.getRelative(BlockFace.UP), false);
			return true;
		}
		
		@Override public String toString() {
			return "loc: "+loc+"CloseGate";
		}
		@Override
		public ActionType getActionType() {
			return ActionType.CLOSE_GATE;
		}
	}
	
	
	
	
	/*
	public boolean onNavComplete(ChestCharacter3 ChestCharacter3) {
		Debug.out(ChestCharacter3.getName()+ ": waypoint "+wayPointNext+ " reached!");
		GoAndDo wayPointReached = wayPoints.get(wayPointNext);
//		boolean actionCompleted = // should be true!
				wayPointReached.doAction(ChestCharacter3); 
		
		boolean destinationReached  = wayPointNext == wayPoints.size()-1;
		wayPointNext++;
		if (!destinationReached)
			ChestCharacter3.goTo(wayPoints.get(wayPointNext).loc);
		return destinationReached;
	}
	*/

//	public abstract void onNavCancel(ChestCharacter3 npc);
//	public abstract Navigator clone();	
	
	@SuppressWarnings("unchecked")
	public Navigator clone() {
		return new Navigator((LinkedList<GoAndDo>) wayPoints.clone());
	}
	
	
	public Navigator reversed() {
		Navigator ret = this.clone();
		LinkedList<GoAndDo> revWayPoints = new LinkedList<GoAndDo>();
		
		for (int i = wayPoints.size()-1; i >= 0; i--) {
			GoAndDo wp = wayPoints.get(i);
			if (wp instanceof GoAndCloseGate)
			{
				GoAndCloseGate wpClose = (GoAndCloseGate) wp;
				revWayPoints.add(new GoAndOpenGate(wpClose.loc, wpClose.gate));
			}
			else if (wp instanceof GoAndOpenGate)
			{
				GoAndOpenGate wpOpen = (GoAndOpenGate) wp;
				revWayPoints.add(new GoAndCloseGate(wpOpen.loc, wpOpen.gate));
			}
			else 
				revWayPoints.add(wp);
				
		}
		ret.wayPoints = revWayPoints;
		return ret;
	}
	
	public Action toAction(final Character npc, final Action next, final Action cantNavigateNextActionAfterTeleport) {
		return toAction(npc, next, ActionContainer.neww(cantNavigateNextActionAfterTeleport), 0);
	}
	public Action toAction(final Character npc, final Action next, final ActionContainer cantNavigateNextActionAfterTeleport) {
		return toAction(npc, next, cantNavigateNextActionAfterTeleport, 0);
	}
	private Action toAction(final Character npc, final Action next, final ActionContainer cantNavigateNextActionAfterTeleport, final int wayPointNext) {
		if (wayPointNext >= wayPoints.size())
		{
//			Debug.out("Navigator complete! going to do "+next.getActionType());
			return next;
		}
		// otherwise: 
		final GoAndDo currentWayPoint = wayPoints.get(wayPointNext);
		return new Action() {

			@Override
			public Location getLocation() {
				return currentWayPoint.loc;
			}

			@Override
			public Action doAction() {
				currentWayPoint.doAction(npc);
				return toAction(npc, next, cantNavigateNextActionAfterTeleport, wayPointNext+1);
			}

			@Override
			public long getWaitingTime() {
				return 0;
			}

			@Override
			public Action cantNavigate() {
				if (currentWayPoint.getActionType() == ActionType.CLOSE_GATE)
				{
					npc.teleport(getLocation());
					return toAction(npc, next, cantNavigateNextActionAfterTeleport, wayPointNext+1);
				}
				Debug.out("Can't follow navigator!");
				npc.teleport(npc.getHomeLocation());
				return cantNavigateNextActionAfterTeleport.getAction();
			}
			@Override
			public ActionType getActionType() {
				return currentWayPoint.getActionType();
			}
			};
		
	}
	
	public static Navigator getNavigator(Location from, final Location to, int maxNblocks, BlockChecker walkOnlyOn) {
		BlockChecker blockChecker = new BlockChecker(){
			@Override
			public boolean isValid(Block block) {
				return block.getLocation().distanceSquared(to) < 2 ; // block.getWorld().getBlockAt(to).equals(block);
			}};
			
		WalkingGroundFinder.WalkingGroundFinderResult result = WalkingGroundFinder.closestBlockOnWalkableGround(
				from, maxNblocks, blockChecker, null, walkOnlyOn);
		if (result == null)
			return new Navigator();
		return new Navigator(result.wayPoints);
	}
}
