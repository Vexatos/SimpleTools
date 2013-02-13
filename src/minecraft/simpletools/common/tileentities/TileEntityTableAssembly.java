package simpletools.common.tileentities;

import com.google.common.io.ByteArrayDataInput;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISidedInventory;
import simpletools.common.interfaces.IAttachment;
import simpletools.common.interfaces.ICore;
import universalelectricity.core.implement.IItemElectric;
import universalelectricity.core.implement.IJouleStorage;
import universalelectricity.prefab.implement.IRedstoneProvider;
import universalelectricity.prefab.network.IPacketReceiver;
import universalelectricity.prefab.tile.TileEntityAdvanced;

public class TileEntityTableAssembly extends TileEntityAdvanced implements IRedstoneProvider, IPacketReceiver, ISidedInventory
{
	private ItemStack[] inventory = new ItemStack[4];
	private int playersUsing = 0;

	@Override
	public int getSizeInventory()
	{
		return 4;
	}

	@Override
	public ItemStack getStackInSlot(int var1)
	{
		if (var1 < this.getSizeInventory())
			return this.inventory[var1];
		else return null;
	}

	@Override
	public ItemStack decrStackSize(int var1, int var2)
	{
		if (this.inventory[var1] != null && this.inventory[var1].stackSize > var2)
		{
			this.inventory[var1].stackSize -= var2;
			return new ItemStack(this.inventory[var1].itemID, var2, this.inventory[var1].getItemDamage());
		}
		else if (this.inventory[var1] != null && this.inventory[var1].stackSize == var2)
		{
			ItemStack toReturn = new ItemStack(this.inventory[var1].itemID, var2, this.inventory[var1].getItemDamage());
			this.inventory[var1] = null;
			return toReturn;
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1)
	{
		return this.inventory[var1];
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2)
	{
		this.inventory[var1] = var2;
	}

	@Override
	public String getInvName()
	{
		return "TileEntityAssemblyTable";
	}

	@Override
	public int getInventoryStackLimit()
	{
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1)
	{
		return true;
	}
	
	@Override
	public void openChest()
	{
		this.playersUsing ++;
	}

	@Override
	public void closeChest()
	{
		this.playersUsing--;
	}

	@Override
	public int getStartInventorySide(ForgeDirection side)
	{
		switch (side.ordinal())
		{
			case 0:		return 0;
			case 1:		return 2;
			case 2:		
			case 3:		
			case 4:		
			case 5:		
			default:	return 1;
		}
	}

	@Override
	public int getSizeInventorySide(ForgeDirection side)
	{
		return 1;
	}

	@Override
	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream)
	{
		
	}

	@Override
	public boolean isPoweringTo(ForgeDirection side)
	{
		return this.canProcess();
	}

	@Override
	public boolean isIndirectlyPoweringTo(ForgeDirection side)
	{
		return this.canProcess();
	}

	private boolean canProcess()
	{
		ItemStack slot0 = this.inventory[0];
		ItemStack slot1 = this.inventory[1];
		ItemStack slot2 = this.inventory[2];
		
		if (slot0.getItem() instanceof IAttachment && slot1.getItem() instanceof ICore)
		{
			IAttachment attachTemp = (IAttachment)slot0.getItem();
			ICore coreTemp = (ICore)slot1.getItem();
			
			if (attachTemp.getToolAttachmentType(slot0) == coreTemp.getCoreType(slot1) && attachTemp.getMinimumTier(slot0) <= coreTemp.getCoreTier(slot1))
			{
				if (coreTemp.requiresElectricity(slot1) && slot2.getItem() instanceof IItemElectric && ((IItemElectric)slot2.getItem()).canProduceElectricity() )
				{
					return true;
				}
				// TODO add support for fuel
				
			}
		}
		return false;
	}
}
