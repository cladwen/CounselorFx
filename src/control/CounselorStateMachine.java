/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Loading, NoWorld, OrderEntry, ReadOnly
 *
 * @author jmoura
 */
public class CounselorStateMachine {

    private static final Log log = LogFactory.getLog(WorldFacadeCounselor.class);
    private static CounselorStateMachine instance;
    private final State[] states = {new StateLoading(), new StateNoWorld(), new StateOrderEntry(), new StateReadOnly()};
    private State currentState;
    private static final int LOADING = 0;
    private static final int NOWORLD = 1;
    private static final int ORDERENTRY = 2;
    private static final int READONLY = 3;
    private String worldFilename = "";

    private CounselorStateMachine() {
        this.currentState = states[LOADING];
    }

    public static synchronized CounselorStateMachine getInstance() {
        if (CounselorStateMachine.instance == null) {
            CounselorStateMachine.instance = new CounselorStateMachine();
        }
        return CounselorStateMachine.instance;
    }

    /**
     * @return the currentState
     */
    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentStateLoading() {
        this.currentState = states[LOADING];
    }

    public void setCurrentStateNoWorld() {
        this.currentState = states[NOWORLD];
    }

    public void setCurrentStateOrderEntry() {
        this.currentState = states[ORDERENTRY];
    }

    public void setCurrentStateReadOnly() {
        this.currentState = states[READONLY];
    }

    public abstract class State {

        public boolean isLoading() {
            return false;
        }

        public boolean isNoWorld() {
            return false;
        }

        public boolean isOrderEntry() {
            return false;
        }

        public boolean isReadOnly() {
            return false;
        }

        public boolean isWorldLoaded() {
            return false;
        }
    }

    class StateLoading extends State {

        @Override
        public boolean isLoading() {
            return true;
        }
    }

    class StateNoWorld extends State {

        @Override
        public boolean isNoWorld() {
            return true;
        }
    }

    class StateOrderEntry extends State {

        @Override
        public boolean isWorldLoaded() {
            return true;
        }

        @Override
        public boolean isOrderEntry() {
            return true;
        }

    }

    class StateReadOnly extends State {

        @Override
        public boolean isWorldLoaded() {
            return true;
        }

        @Override
        public boolean isReadOnly() {
            return true;
        }
    }

    /**
     *
     * @param resultsFile
     */
    public void setWorldFilename(String resultsFile) {
        this.worldFilename = resultsFile;
    }

    /**
     *
     * @return
     */
    public String getWorldFilename() {
        return this.worldFilename;
    }
}
