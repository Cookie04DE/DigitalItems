package cookie04.digitalitems.common.luameta;

import com.google.common.collect.Multimap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ToolType;

import java.util.*;

public class LuaItem {
    public static Map<String, Object> get(ItemStack stack) {
        if(stack.isEmpty())
            return Collections.emptyMap();
        HashMap<String, Object> data = new HashMap<>();
        data.put("displayName", stack.getDisplayName().getString());
        data.put("rawName", stack.getTranslationKey());
        data.put("count", stack.getCount());
        data.put("maxCount", stack.getMaxStackSize());
        data.put("damage", stack.getDamage());
        data.put("maxDamage", stack.getMaxDamage());
        HashMap<String, Integer> enchants = new HashMap<>();
        for(Map.Entry<Enchantment, Integer> enchant : EnchantmentHelper.deserializeEnchantments(stack.getEnchantmentTagList()).entrySet()) {
            enchants.put(enchant.getKey().getName(), enchant.getValue());
        }
        data.put("enchants", enchants);
        data.put("repairCost", stack.getRepairCost());
        data.put("useAction", stack.getUseAction().toString());
        data.put("useDuration", stack.getUseDuration());
        data.put("burnTime", stack.getBurnTime());
        HashMap<String, HashMap<String, Double>> attributes = new HashMap<>();
        attributes.put("mainhand", addAttributesOfEquipSlot(stack, EquipmentSlotType.MAINHAND));
        attributes.put("offhand", addAttributesOfEquipSlot(stack, EquipmentSlotType.OFFHAND));
        attributes.put("head", addAttributesOfEquipSlot(stack, EquipmentSlotType.HEAD));
        attributes.put("chest", addAttributesOfEquipSlot(stack, EquipmentSlotType.CHEST));
        attributes.put("legs", addAttributesOfEquipSlot(stack, EquipmentSlotType.LEGS));
        attributes.put("feet", addAttributesOfEquipSlot(stack, EquipmentSlotType.FEET));
        data.put("attributes", attributes);
        data.put("itemEnchantability", stack.getItemEnchantability());
        ArrayList<String> toolTypes = new ArrayList<>();
        for(ToolType type : stack.getToolTypes()) {
            toolTypes.add(type.toString());
        }
        data.put("toolTypes", toolTypes);
        data.put("xpRepairRatio", stack.getXpRepairRatio());
        return data;
    }

    private static HashMap<String, Double> addAttributesOfEquipSlot(ItemStack stack, EquipmentSlotType type) {
        HashMap<String, Double> toReturn = new HashMap<>();
        Multimap<Attribute, AttributeModifier> attributes = stack.getAttributeModifiers(type);
        if(attributes.isEmpty()) {
            return toReturn;
        }
        for(Map.Entry<Attribute, AttributeModifier> entry : attributes.entries()) {
            AttributeModifier mod = entry.getValue();
            toReturn.put(mod.getName(), mod.getAmount());
        }
        return toReturn;
    }
}
