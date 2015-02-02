package commands;

import java.util.Arrays;

import main.Debug;
import main.MCity;

import com.massivecraft.massivecore.util.Txt;



public class CmdMCityDebug extends MCCommand{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdMCityDebug()
	{
		// Aliases
		this.addAliases("d", "debug");

		// Args
		this.addOptionalArg("optionalArg", "");
		
		this.setDesc("debug something");
		this.setHelp("This command is used to try out anything");
		
		// Requirements
//		this.addRequirements(ReqFactionsEnabled.get());
//		this.addRequirements(ReqHasPerm.get(Perm.LIST.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void perform()
	{
		if(player.isOp())
		{
	//		int defaultInt = 1;
	//		int index = 0;
	//		Integer integerArg = this.arg(index, ARInteger.get(), defaultInt);
	//		if (integerArg == null) 
	//			return;
	
			
			sendMessage("performing debug command!");
			
			// config.yml shite:
	//		LinkedList<ItemStack> q = new LinkedList<ItemStack>(Arrays.asList(new ItemStack[]{new ItemStack(Material.ACACIA_STAIRS, 4), new ItemStack(Material.APPLE, 2)}));
	//		LinkedList<Material> w = new LinkedList<Material>(Arrays.asList(new Material[]{Material.ACACIA_STAIRS, Material.APPLE}));
	//		
	//		FileConfiguration conf = MCity.getCurrentPlugin().getConfig();
	//		conf.set("randomBullshit", w);
	//		conf.set("randomBullshit2", q);
	//		conf.set("randomBullshit3", new LinkedList<ItemStack>());
	//		try {
	//			conf.save(new File("BStry.yml"));
	//		} catch (IOException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//		
	//		MCity.getCurrentPlugin().saveConfig();
			
			
	//		if (this.arg(0) != null)
	//			sendMessage(Txt.parse(this.arg(0)));
			
	//		playerCity.statistics.statistics = new CityStatistics.Statistics(playerCity.statistics);
	//		playerCity.statistics.statistics.computeNetto((24000-MCity.defaultWorld.getTime())/1000.);
	//		sendMessage(Txt.parse(""+playerCity.statistics.statistics));
		
			
			/*
			player.openInventory(new InventoryView() {
				
				@Override
				public InventoryType getType() {
					return InventoryType.MERCHANT;
				}
				
				@Override
				public Inventory getTopInventory() {
					MerchantInventory ret = new TradeInventory();
					return ret;
				}
				
				@Override
				public HumanEntity getPlayer() {
					return player;
				}
				
				@Override
				public Inventory getBottomInventory() {
					// TODO Auto-generated method stub
					return new PlayerInventory() {
					};
				}
			});
			 */
			
	//		Villager villager = (Villager) player.getWorld().spawnCreature(player.getLocation(), EntityType.VILLAGER);
			
	//		VillagerNBT q;
	//		NBTEditor w;
	//		
	//		VillagerNBT nbt = (VillagerNBT) VillagerNBT.fromEntity(villager);
	//		ItemStack is = new ItemStack(Material.EMERALD, 2);
	//		VillagerNBTOffer offer = new VillagerNBTOffer(is, is, is);
	//		nbt.clearOffers();
	//		nbt.addOffer(offer);
			
	//		NBTContainerEntity q = new NBTContainerEntity(villager);
	//		Debug.out(q.getTag());
					
			
			
			
	//		try {
	//			MCity.getCurrentPlugin().getConfig().save("magwegConfig");
	//		} catch (IOException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
			
			
			
	//		List<Location> places = MCity.getCity(player).townHall.bedsRequirement.freeLocations;
	//		for (Location p : places)
	//		{
	//			p.getWorld().spawn(p, Villager.class);
	//		}
			
			Debug.out("total consumption = "+Arrays.toString(MCity.getCity(player).getTotalConsumption()));
			sendMessage("total consumption = "+Arrays.toString(MCity.getCity(player).getTotalConsumption()));
		}
		else
		{
			sendMessage(Txt.parse("<bad>You must be OP to do that!"));
		}
	}

}
