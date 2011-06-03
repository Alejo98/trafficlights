/*
 * App.java
 */

package tf.gui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class App extends SingleFrameApplication {
    private static Log s_log = LogFactory.getLog(App.class);

    private AppSessionData mSessionData;


    /**
     * @return The session data object, used to save data across sessions.
     * The returned bean will be preserved when the application shuts down.
     * This property is NULL until startup() has completed, after which it
     * will never be null.
     */
    public AppSessionData getSessionData() {
        return mSessionData;
    }

    public void setSessionData(AppSessionData sessionData) {
        this.mSessionData = sessionData;
    }

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        setSessionData(new AppSessionData());
        show(new View(this));
    }
   
    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of App
     */
    public static App getApplication() {
        return Application.getInstance(App.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(App.class, args);
    }
}
