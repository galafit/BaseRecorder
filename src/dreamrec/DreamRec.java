package dreamrec;

import device.BdfDataSourceActive;

/**
 * Created by IntelliJ IDEA.
 * User: galafit
 * Date: 03/10/14
 * Time: 10:43
 * To change this template use File | Settings | File Templates.
 */
public class DreamRec {
    public static void main(String[] args) {
        ApparatModel model = new ApparatModel();
        ApplicationProperties appProperties = new ApplicationProperties();
        BdfDataSourceActive device = appProperties.getDeviceImplementation();
        ControllerNew controller = new ControllerNew(model, device);
        MainViewNew mainWindow = new MainViewNew(model, controller);
        controller.setMainWindow(mainWindow);
    }
}