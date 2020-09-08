package cookie04.digitalitems.client;

import cookie04.digitalitems.Registration;
import cookie04.digitalitems.client.screens.ItemDigitizerScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class SetupClient {
    public static void init(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(Registration.ITEM_DIGITIZER_CONTAINER.get(), ItemDigitizerScreen::new);
    }
}
