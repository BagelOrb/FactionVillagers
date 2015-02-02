package interaction;

import main.Debug;
import main.FactionVillagers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.block.NotePlayEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreeperPowerEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.HorseJumpEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.entity.PigZapEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.SheepDyeWoolEvent;
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.painting.PaintingBreakEvent;
import org.bukkit.event.painting.PaintingPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChannelEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.event.weather.LightningStrikeEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.SpawnChangeEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;

//AsyncPlayerPreLoginEvent, BlockEvent, EntityEvent, HangingEvent, InventoryEvent, InventoryMoveItemEvent, InventoryPickupItemEvent, PaintingEvent, PlayerEvent, PlayerLeashEntityEvent, PlayerPreLoginEvent, ServerEvent, VehicleEvent, WeatherEvent, WorldEvent
@SuppressWarnings("deprecation")
public class AllListener implements Listener {
	public AllListener(FactionVillagers mCity) {
		// TODO Auto-generated constructor stub
	}

	
	
	public static void main(String[] args) {
//		String basic = "PlayerPreLoginEvent";
		
		
		
		String player = "AsyncPlayerPreLoginEvent, AsyncPlayerChatEvent, PlayerAchievementAwardedEvent, PlayerAnimationEvent, PlayerBedEnterEvent, PlayerBedLeaveEvent, PlayerBucketEmptyEvent, PlayerBucketFillEvent, PlayerChangedWorldEvent, PlayerChannelEvent, PlayerChatEvent, PlayerChatTabCompleteEvent, PlayerCommandPreprocessEvent, PlayerDropItemEvent, PlayerEditBookEvent, PlayerEggThrowEvent, PlayerExpChangeEvent, PlayerFishEvent, PlayerGameModeChangeEvent, PlayerInteractEntityEvent, PlayerInteractEvent, PlayerItemBreakEvent, PlayerItemConsumeEvent, PlayerItemHeldEvent, PlayerJoinEvent, PlayerKickEvent, PlayerLevelChangeEvent, PlayerLoginEvent, PlayerMoveEvent, PlayerPickupItemEvent, PlayerQuitEvent, PlayerRespawnEvent, PlayerShearEntityEvent, PlayerStatisticIncrementEvent, PlayerToggleFlightEvent, PlayerToggleSneakEvent, PlayerToggleSprintEvent, PlayerVelocityEvent";
		// PlayerInventoryEvent,  = deprecated!
		String block = "BlockBurnEvent, BlockCanBuildEvent, BlockDamageEvent, BlockDispenseEvent, BlockExpEvent, BlockFadeEvent, BlockFromToEvent, BlockGrowEvent, BlockIgniteEvent, BlockPhysicsEvent, BlockPistonExtendEvent, BlockPistonRetractEvent, BlockPlaceEvent, BlockRedstoneEvent, LeavesDecayEvent, NotePlayEvent, SignChangeEvent";
		String entity = "PlayerLeashEntityEvent, CreatureSpawnEvent, CreeperPowerEvent, EntityChangeBlockEvent, EntityCombustEvent, EntityCreatePortalEvent, EntityDamageEvent, EntityDeathEvent, EntityExplodeEvent, EntityInteractEvent, EntityPortalEnterEvent, EntityRegainHealthEvent, EntityShootBowEvent, EntityTameEvent, EntityTargetEvent, EntityTeleportEvent, EntityUnleashEvent, ExplosionPrimeEvent, FoodLevelChangeEvent, HorseJumpEvent, ItemDespawnEvent, ItemSpawnEvent, PigZapEvent, ProjectileHitEvent, ProjectileLaunchEvent, SheepDyeWoolEvent, SheepRegrowWoolEvent, SlimeSplitEvent";
		String hanging = "HangingBreakEvent, HangingPlaceEvent";
		String inventory = "InventoryMoveItemEvent, InventoryPickupItemEvent, InventoryCloseEvent, InventoryInteractEvent, InventoryOpenEvent, PrepareItemCraftEvent, BrewEvent, FurnaceBurnEvent, FurnaceSmeltEvent";
		String enchantment = "EnchantItemEvent, PrepareItemEnchantEvent";
		String painting = "PaintingBreakEvent, PaintingPlaceEvent";
		String server = "MapInitializeEvent, PluginDisableEvent, PluginEnableEvent, ServerCommandEvent, ServerListPingEvent, ServiceRegisterEvent, ServiceUnregisterEvent";
		String vehicle = "VehicleBlockCollisionEvent, VehicleEntityCollisionEvent, VehicleCreateEvent, VehicleDamageEvent, VehicleDestroyEvent, VehicleEnterEvent, VehicleExitEvent, VehicleMoveEvent, VehicleUpdateEvent";
		String weather = "LightningStrikeEvent, ThunderChangeEvent, WeatherChangeEvent";
		String world = "ChunkLoadEvent, ChunkPopulateEvent, ChunkUnloadEvent, PortalCreateEvent, SpawnChangeEvent, StructureGrowEvent, WorldInitEvent, WorldLoadEvent, WorldSaveEvent, WorldUnloadEvent";
		
		
		
		
		checkHanderList("org.bukkit.event.player.", player);
		checkHanderList("org.bukkit.event.block.", block);
		checkHanderList("org.bukkit.event.entity.", entity);
		checkHanderList("org.bukkit.event.hanging.", hanging);
		checkHanderList("org.bukkit.event.inventory.", inventory);
		checkHanderList("org.bukkit.event.enchantment.", enchantment);
		checkHanderList("org.bukkit.event.painting.", painting);
		checkHanderList("org.bukkit.event.server.", server);
		checkHanderList("org.bukkit.event.vehicle.", vehicle);
		checkHanderList("org.bukkit.event.weather.", weather);
		checkHanderList("org.bukkit.event.world.", world);
		
		
		String[] allEvents = new String[]{player, block, entity, hanging, inventory, enchantment, painting, server, vehicle, weather, world};
		String[] allEventsNames = new String[]{"player", "block", "entity", "hanging", "inventory", "enchantment", "painting", "server", "vehicle", "weather", "world"};
		
		for (int i = 0; i< allEvents.length; i++)
		{
			String eventsString = allEvents[i];
			String[] events = eventsString
					.split(", ");
			System.out.println("\r\n\r\n\t// "+allEventsNames[i]);
			for (String event : events)
				System.out.println("\t@EventHandler(priority = EventPriority.MONITOR)\r\n\t public void onEvent("+event+" event) {	\r\n"
						+ "\t\tDebugOut(\""+event+"\"); }");
		}
		
	}
	
	public static void checkHanderList(String importPath, String events) {
//		System.out.println(importPath);
		for (String event : events.split(", "))
			try {
				Class<?> clazz = Class.forName(importPath+event);
				clazz.getMethod("getHandlerList").invoke(clazz);
			} catch (Exception e) { 
				System.out.println(event);
				e.printStackTrace(); }
		
	}

	
	
	
	
	
	
	
	


	// player
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(AsyncPlayerPreLoginEvent event) {	
		DebugOut("AsyncPlayerPreLoginEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(AsyncPlayerChatEvent event) {	
		DebugOut("AsyncPlayerChatEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerAchievementAwardedEvent event) {	
		DebugOut("PlayerAchievementAwardedEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerAnimationEvent event) {	
		DebugOut("PlayerAnimationEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerBedEnterEvent event) {	
		DebugOut("PlayerBedEnterEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerBedLeaveEvent event) {	
		DebugOut("PlayerBedLeaveEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerBucketEmptyEvent event) {	
		DebugOut("PlayerBucketEmptyEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerBucketFillEvent event) {	
		DebugOut("PlayerBucketFillEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerChangedWorldEvent event) {	
		DebugOut("PlayerChangedWorldEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerChannelEvent event) {	
		DebugOut("PlayerChannelEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerChatEvent event) {	
		DebugOut("PlayerChatEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerChatTabCompleteEvent event) {	
		DebugOut("PlayerChatTabCompleteEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerCommandPreprocessEvent event) {	
		DebugOut("PlayerCommandPreprocessEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerDropItemEvent event) {	
		DebugOut("PlayerDropItemEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerEditBookEvent event) {	
		DebugOut("PlayerEditBookEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerEggThrowEvent event) {	
		DebugOut("PlayerEggThrowEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerExpChangeEvent event) {	
		DebugOut("PlayerExpChangeEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerFishEvent event) {	
		DebugOut("PlayerFishEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerGameModeChangeEvent event) {	
		DebugOut("PlayerGameModeChangeEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerInteractEntityEvent event) {	
		DebugOut("PlayerInteractEntityEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerInteractEvent event) {	
		DebugOut("PlayerInteractEvent"); }
//	@EventHandler(priority = EventPriority.MONITOR)
//	 public void onEvent(PlayerInventoryEvent event) {	
//		DebugOut("PlayerInventoryEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerItemBreakEvent event) {	
		DebugOut("PlayerItemBreakEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerItemConsumeEvent event) {	
		DebugOut("PlayerItemConsumeEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerItemHeldEvent event) {	
		DebugOut("PlayerItemHeldEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerJoinEvent event) {	
		DebugOut("PlayerJoinEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerKickEvent event) {	
		DebugOut("PlayerKickEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerLevelChangeEvent event) {	
		DebugOut("PlayerLevelChangeEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerLoginEvent event) {	
		DebugOut("PlayerLoginEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerMoveEvent event) {	
//		DebugOut("PlayerMoveEvent"); 
		}
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerPickupItemEvent event) {	
		DebugOut("PlayerPickupItemEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerQuitEvent event) {	
		DebugOut("PlayerQuitEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerRespawnEvent event) {	
		DebugOut("PlayerRespawnEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerShearEntityEvent event) {	
		DebugOut("PlayerShearEntityEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerStatisticIncrementEvent event) {	
		DebugOut("PlayerStatisticIncrementEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerToggleFlightEvent event) {	
		DebugOut("PlayerToggleFlightEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerToggleSneakEvent event) {	
		DebugOut("PlayerToggleSneakEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerToggleSprintEvent event) {	
		DebugOut("PlayerToggleSprintEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerVelocityEvent event) {	
		DebugOut("PlayerVelocityEvent"); }


	// block
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(BlockBurnEvent event) {	
		DebugOut("BlockBurnEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(BlockCanBuildEvent event) {	
		DebugOut("BlockCanBuildEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(BlockDamageEvent event) {	
		DebugOut("BlockDamageEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(BlockDispenseEvent event) {	
		DebugOut("BlockDispenseEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(BlockExpEvent event) {	
		DebugOut("BlockExpEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(BlockFadeEvent event) {	
		DebugOut("BlockFadeEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(BlockFromToEvent event) {	
		DebugOut("BlockFromToEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(BlockGrowEvent event) {	
		DebugOut("BlockGrowEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(BlockIgniteEvent event) {	
		DebugOut("BlockIgniteEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(BlockPhysicsEvent event) {	
		DebugOut("BlockPhysicsEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(BlockPistonExtendEvent event) {	
		DebugOut("BlockPistonExtendEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(BlockPistonRetractEvent event) {	
		DebugOut("BlockPistonRetractEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(BlockPlaceEvent event) {	
		DebugOut("BlockPlaceEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(BlockRedstoneEvent event) {	
		DebugOut("BlockRedstoneEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(LeavesDecayEvent event) {	
		DebugOut("LeavesDecayEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(NotePlayEvent event) {	
		DebugOut("NotePlayEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(SignChangeEvent event) {	
		DebugOut("SignChangeEvent"); }


	// entity
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PlayerLeashEntityEvent event) {	
		DebugOut("PlayerLeashEntityEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(CreatureSpawnEvent event) {	
		DebugOut("CreatureSpawnEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(CreeperPowerEvent event) {	
		DebugOut("CreeperPowerEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(EntityChangeBlockEvent event) {	
		DebugOut("EntityChangeBlockEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(EntityCombustEvent event) {	
		DebugOut("EntityCombustEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(EntityCreatePortalEvent event) {	
		DebugOut("EntityCreatePortalEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(EntityDamageEvent event) {	
		DebugOut("EntityDamageEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(EntityDeathEvent event) {	
		DebugOut("EntityDeathEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(EntityExplodeEvent event) {	
		DebugOut("EntityExplodeEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(EntityInteractEvent event) {	
		DebugOut("EntityInteractEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(EntityPortalEnterEvent event) {	
		DebugOut("EntityPortalEnterEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(EntityRegainHealthEvent event) {	
		DebugOut("EntityRegainHealthEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(EntityShootBowEvent event) {	
		DebugOut("EntityShootBowEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(EntityTameEvent event) {	
		DebugOut("EntityTameEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(EntityTargetEvent event) {	
		DebugOut("EntityTargetEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(EntityTeleportEvent event) {	
		DebugOut("EntityTeleportEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(EntityUnleashEvent event) {	
		DebugOut("EntityUnleashEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(ExplosionPrimeEvent event) {	
		DebugOut("ExplosionPrimeEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(FoodLevelChangeEvent event) {	
		DebugOut("FoodLevelChangeEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(HorseJumpEvent event) {	
		DebugOut("HorseJumpEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(ItemDespawnEvent event) {	
		DebugOut("ItemDespawnEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(ItemSpawnEvent event) {	
		DebugOut("ItemSpawnEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PigZapEvent event) {	
		DebugOut("PigZapEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(ProjectileHitEvent event) {	
		DebugOut("ProjectileHitEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(ProjectileLaunchEvent event) {	
		DebugOut("ProjectileLaunchEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(SheepDyeWoolEvent event) {	
		DebugOut("SheepDyeWoolEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(SheepRegrowWoolEvent event) {	
		DebugOut("SheepRegrowWoolEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(SlimeSplitEvent event) {	
		DebugOut("SlimeSplitEvent"); }


	// hanging
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(HangingBreakEvent event) {	
		DebugOut("HangingBreakEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(HangingPlaceEvent event) {	
		DebugOut("HangingPlaceEvent"); }


	// inventory
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(InventoryMoveItemEvent event) {	
		DebugOut("InventoryMoveItemEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(InventoryPickupItemEvent event) {	
		DebugOut("InventoryPickupItemEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(InventoryCloseEvent event) {	
		DebugOut("InventoryCloseEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(InventoryInteractEvent event) {	
		DebugOut("InventoryInteractEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(InventoryOpenEvent event) {	
		DebugOut("InventoryOpenEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PrepareItemCraftEvent event) {	
		DebugOut("PrepareItemCraftEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(BrewEvent event) {	
		DebugOut("BrewEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(FurnaceBurnEvent event) {	
		DebugOut("FurnaceBurnEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(FurnaceSmeltEvent event) {	
		DebugOut("FurnaceSmeltEvent"); }


	// enchantment
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(EnchantItemEvent event) {	
		DebugOut("EnchantItemEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PrepareItemEnchantEvent event) {	
		DebugOut("PrepareItemEnchantEvent"); }


	// painting
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PaintingBreakEvent event) {	
		DebugOut("PaintingBreakEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PaintingPlaceEvent event) {	
		DebugOut("PaintingPlaceEvent"); }


	// server
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(MapInitializeEvent event) {	
		DebugOut("MapInitializeEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PluginDisableEvent event) {	
		DebugOut("PluginDisableEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PluginEnableEvent event) {	
		DebugOut("PluginEnableEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(ServerCommandEvent event) {	
		DebugOut("ServerCommandEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(ServerListPingEvent event) {	
		DebugOut("ServerListPingEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(ServiceRegisterEvent event) {	
		DebugOut("ServiceRegisterEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(ServiceUnregisterEvent event) {	
		DebugOut("ServiceUnregisterEvent"); }


	// vehicle
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(VehicleBlockCollisionEvent event) {	
		DebugOut("VehicleBlockCollisionEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(VehicleEntityCollisionEvent event) {	
		DebugOut("VehicleEntityCollisionEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(VehicleCreateEvent event) {	
		DebugOut("VehicleCreateEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(VehicleDamageEvent event) {	
		DebugOut("VehicleDamageEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(VehicleDestroyEvent event) {	
		DebugOut("VehicleDestroyEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(VehicleEnterEvent event) {	
		DebugOut("VehicleEnterEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(VehicleExitEvent event) {	
		DebugOut("VehicleExitEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(VehicleMoveEvent event) {	
		DebugOut("VehicleMoveEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(VehicleUpdateEvent event) {	
//		DebugOut("VehicleUpdateEvent");
		}


	// weather
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(LightningStrikeEvent event) {	
		DebugOut("LightningStrikeEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(ThunderChangeEvent event) {	
		DebugOut("ThunderChangeEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(WeatherChangeEvent event) {	
		DebugOut("WeatherChangeEvent"); }


	// world
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(ChunkLoadEvent event) {	
		DebugOut("ChunkLoadEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(ChunkPopulateEvent event) {	
		DebugOut("ChunkPopulateEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(ChunkUnloadEvent event) {	
		DebugOut("ChunkUnloadEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(PortalCreateEvent event) {	
		DebugOut("PortalCreateEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(SpawnChangeEvent event) {	
		DebugOut("SpawnChangeEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(StructureGrowEvent event) {	
		DebugOut("StructureGrowEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(WorldInitEvent event) {	
		DebugOut("WorldInitEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(WorldLoadEvent event) {	
		DebugOut("WorldLoadEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(WorldSaveEvent event) {	
		DebugOut("WorldSaveEvent"); }
	@EventHandler(priority = EventPriority.MONITOR)
	 public void onEvent(WorldUnloadEvent event) {	
		DebugOut("WorldUnloadEvent"); }


	private static void DebugOut(String out) {
		Debug.out(out);
	}
}
