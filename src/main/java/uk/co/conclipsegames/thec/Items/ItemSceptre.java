package uk.co.conclipsegames.thec.Items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import uk.co.conclipsegames.thec.thec;
import uk.co.conclipsegames.thec.util.Helper;

import java.util.List;

import com.mrdimka.hammercore.common.utils.WorldUtil;

/**
 * Created by James on 14/08/2017.
 */
public class ItemSceptre extends Item
{
	protected String name;
	int SceptreType;
	
	public ItemSceptre(String name)
	{
		this.name = name;
		setUnlocalizedName(name);
		setMaxDamage(200);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack item = player.getHeldItem(hand);
		item.damageItem(1, player);
		
		if(item.getItem() == ModItems.sceptreDislocation)
		{
			if(player.isSneaking())
			{
				Helper.changeDoubleTag(item, "tx", player.posX);
				Helper.changeDoubleTag(item, "ty", player.posY);
				Helper.changeDoubleTag(item, "tz", player.posZ);
				Helper.changeDoubleTag(item, "tyaw", player.rotationYaw);
				Helper.changeDoubleTag(item, "tpitch", player.rotationPitch);
				Helper.changeTag(item, "dim", world.provider.getDimension());
				
				if(!world.isRemote)
					player.sendMessage(new TextComponentString("Return location set to: " + player.getPosition().getX() + ", " + player.getPosition().getY() + ", " + player.getPosition().getZ() + " in dimension " + world.provider.getDimension()));
			} else if(player instanceof EntityPlayerMP && item.hasTagCompound())
			{
				if(item.getTagCompound().getInteger("ty") >= 2)
				{
					NBTTagCompound nbt = item.getTagCompound();
					WorldUtil.teleportPlayer((EntityPlayerMP) player, nbt.getInteger("dim"), item.getTagCompound().getDouble("tx"), item.getTagCompound().getDouble("ty"), item.getTagCompound().getDouble("tz"));
					player.rotationYaw = (float) nbt.getDouble("tyaw");
					player.rotationPitch = (float) nbt.getDouble("tpitch");
				}
			}
		}
		if(item.getItem() == ModItems.sceptreUprising)
		{
			Vec3d target = player.getLookVec().add(player.getPositionVector());
			
			player.motionX = (target.x - player.posX) * 3F;
			player.motionY = (target.y - player.posY) * 2F;
			player.motionZ = (target.z - player.posZ) * 3F;
			player.fallDistance = 0;
			
		}
		if(item.getItem() == ModItems.sceptreFreezing)
		{
			BlockPos coords = player.getPosition();
			List<EntityLiving> allentities = world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(coords.getX() - 10, coords.getY() - 10, coords.getZ() - 10, coords.getX() + 15, coords.getY() + 10, coords.getZ() + 10));
			
			int i = 0;
			while(i != allentities.size())
			{
				allentities.get(i).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 1000, 200));
				player.removePotionEffect(MobEffects.SLOWNESS);
				i++;
			}
		}
		
		return super.onItemRightClick(world, player, hand);
	}
}
