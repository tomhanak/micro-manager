///////////////////////////////////////////////////////////////////////////////
//PROJECT:       Micro-Manager
//SUBSYSTEM:     mmstudio
//-----------------------------------------------------------------------------
//
// AUTHOR:       Nenad Amodaj, nenad@amodaj.com, December 3, 2006
//               Chris Weisiger, 2015
//
// COPYRIGHT:    University of California, San Francisco, 2006-2015
//
// LICENSE:      This file is distributed under the BSD license.
//               License text is included with the source distribution.
//
//               This file is distributed in the hope that it will be useful,
//               but WITHOUT ANY WARRANTY; without even the implied warranty
//               of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
//
//               IN NO EVENT SHALL THE COPYRIGHT OWNER OR
//               CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
//               INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES.
//

package org.micromanager;

import java.util.List;

import org.micromanager.data.Datastore;
import org.micromanager.data.Image;

import org.micromanager.display.DisplayWindow;

/**
 * Interface for interacting with the Snap/Live display and live mode.
 */
public interface SnapLiveManager {
   /**
    * Perform a snap and display the results, if desired. Returns the snapped
    * image(s).
    * If live mode is currently on, then instead of performing a snap, the
    * most recent images from live mode will be returned immediately.
    * @param shouldDisplay If true, then the snapped images will be added to
    *        the Datastore and displayed.
    * @return A list of acquired Images from the snap.
    */
   public List<Image> snap(boolean shouldDisplay);

   /**
    * Indicates if live mode is currently running.
    * @return true iff live mode is on.
    */
   public boolean getIsLiveModeOn();

   /**
    * Turns live mode on or off. This will post an
    * org.micromanager.events.LiveModeEvent on the global application event
    * bus.
    * @param isOn If true, then live mode will be activated; otherwise it will
    *        be halted.
    */
   public void setLiveMode(boolean isOn);

   /**
    * Temporarily halt live mode, or re-start it after a temporary halt. This
    * is useful for actions that cannot be performed while live mode is
    * running (such as changing many camera settings), so that live mode can
    * be re-started once the action is complete. Instead of calling
    * getIsLiveModeOn(), stopping it if necessary, and then re-starting it if
    * it was on, you can instead blindly do:
    * setSuspended(true);
    * do something that can't run when live mode is on
    * setSuspended(false);
    * and live mode will only be re-started if it was on to begin with.
    * Note that suspending live mode does not produce LiveModeEvents, as the
    * expectation is that live mode is only suspended for very brief periods.
    * @param shouldSuspend If true, then live mode will be halted if it is
    *        running. If false, and live mode was running when
    *        setSuspended(true) was called, then live mode will be restarted.
    */
   public void setSuspended(boolean shouldSuspend);

   /**
    * Insert the provided image into the Datastore, causing it to be displayed
    * in any open Snap/Live DisplayWindows. If no displays are open or if the
    * image dimensions don't match those of the previously-displayed images,
    * then a new display will be created.
    */
   public void displayImage(Image image);

   /**
    * Return the DisplayWindow used for snap/live mode. May be null if that
    * display has been closed. Snap/live mode only "knows about" the display
    * it itself created -- thus, if the user duplicates that display and then
    * closes the original, you will get null when you call this method.
    */
   public DisplayWindow getDisplay();
}
