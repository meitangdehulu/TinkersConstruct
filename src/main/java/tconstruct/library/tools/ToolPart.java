package tconstruct.library.tools;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import tconstruct.library.TinkerRegistry;
import tconstruct.library.materials.Material;
import tconstruct.library.tinkering.MaterialItem;
import tconstruct.library.tinkering.PartMaterialType;
import tconstruct.library.traits.ITrait;
import tconstruct.library.utils.TagUtil;
import tconstruct.library.utils.Tags;

public class ToolPart extends MaterialItem implements IToolPart {

  public ToolPart() {
    this.setCreativeTab(TinkerRegistry.tabTools);
  }

  @Override
  public void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
    for(Material mat : TinkerRegistry.getAllMaterials()) {
      // check if the material makes sense for this item (is it usable to build stuff?)
      if(canUseMaterial(mat)) {
        subItems.add(getItemstackWithMaterial(mat));
      }
    }
  }

  public boolean canUseMaterial(Material mat) {
    for(ToolCore tool : TinkerRegistry.getTools()) {
      for(PartMaterialType pmt : tool.requiredComponents) {
        if(pmt.isValid(this, mat)) {
          return true;
        }
      }
    }

    return false;
  }

  @SideOnly(Side.CLIENT)
  @Override
  public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
    Material material = getMaterial(stack);

    if(material == Material.UNKNOWN) {
      NBTTagCompound tag = TagUtil.getTagSafe(stack);
      String materialID = tag.getString(Tags.PART_MATERIAL);

      String error;
      if(materialID != null && !materialID.isEmpty()) {
        error = StatCollector.translateToLocalFormatted("tooltip.part.missingMaterial", materialID);
      }
      else {
        error = StatCollector.translateToLocal("tooltip.part.missingInfo");
      }
      tooltip.add(error);
    }
    else {
      tooltip.add(material.textColor.toString() + EnumChatFormatting.ITALIC.toString() + material.getLocalizedName());

      for(ITrait trait : material.getAllTraits()) {
        tooltip.add(material.textColor + trait.getLocalizedName());
      }
    }

    if(advanced) {
      String materialInfo = StatCollector.translateToLocalFormatted("tooltip.part.materialAddedBy",
                                                                    TinkerRegistry.getTrace(material));
      tooltip.add(materialInfo);
    }
  }
}
