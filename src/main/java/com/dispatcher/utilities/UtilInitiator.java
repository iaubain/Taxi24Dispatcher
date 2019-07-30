/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.dispatcher.utilities;

import com.dispatcher.config.CmdConfig;
import static com.dispatcher.config.CmdConfig.ADD_COMMAND;
import com.dispatcher.config.HeaderConfig;
import com.dispatcher.config.SystemConfig;
import com.dispatcher.entities.Command;
import com.dispatcher.entities.SystemComponent;
import com.dispatcher.remote.ExternalService;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.MediaType;

/**
 *
 * @author Aubain
 */
public class UtilInitiator implements Runnable{
    @Override
    public void run() {
        List<SystemComponent> systemcomponents = new Handler().start();
        if(systemcomponents == null || systemcomponents.isEmpty()){
            Log.d(getClass(), "Error initiating, we have an empty Systemcomponents entity");
            return;
        }
        try {
            Thread.sleep(1000*60);
        } catch (InterruptedException ex) {
            ex.printStackTrace(out);
            Log.d(getClass(), "Error initiating, Thread was interupted");
            return;
        }
        try {
            List<SystemComponent> results = ExternalService.INIT(systemcomponents);
            if(results != null && !results.isEmpty())
                Log.d(getClass(), SystemConfig.SYSTEM_ID[1]+" Successfully Initiated to the dispatcher.");
            else
                Log.d(getClass(), SystemConfig.SYSTEM_ID[1]+" Failed Initiation to the dispatcher.");
        } catch (Exception e) {
            Log.e(getClass(), SystemConfig.SYSTEM_ID[1]+"_"+e.getMessage());
        }
    }
    class Handler{
        public List<SystemComponent> start(){
            List<SystemComponent> mSystemComponent = new ArrayList<>();
            SystemComponent Systemcomponent = new SystemComponent();
            Systemcomponent.setName(SystemConfig.SYSTEM_ID[1]);
            List<Command> mCommand = new ArrayList<>();
            
            for(CmdConfig cmd : CmdConfig.values()){
                Command cmdModel = new Command();
                switch(cmd){
                    case INIT_COMPONENT:
                        cmdModel.setCommand(CmdConfig.INIT_COMPONENT.toString());
                        cmdModel.setCommandDescr("This command is used to initiate a Systemcomponent.");
                        cmdModel.setExported(true);
                        cmdModel.setMethod("POST");
                        cmdModel.setUri("http://localhost:9001/Taxi24Dispatcher/dispatcher/process/fact");
                        cmdModel.setRequestHeader(HeaderConfig.CMD+":"+CmdConfig.INIT_COMPONENT.toString()+"; Content-Type:"+MediaType.APPLICATION_JSON);
                        cmdModel.setRequest("["+new SystemComponent().toString()+"]");
                        cmdModel.setResponse("["+new SystemComponent().toString()+"]");
                        
                        mCommand.add(cmdModel);
                        break;

                    case ADD_COMMAND:
                        cmdModel.setCommand(CmdConfig.ADD_COMMAND.toString());
                        cmdModel.setCommandDescr("This command is used to add a command to an initiated Systemcomponents.");
                        cmdModel.setExported(true);
                        cmdModel.setMethod("POST");
                        cmdModel.setUri("http://localhost:9001/Taxi24Dispatcher/dispatcher/process/fact");
                        cmdModel.setRequestHeader(HeaderConfig.CMD+":"+CmdConfig.ADD_COMMAND.toString()+"; Content-Type:"+MediaType.APPLICATION_JSON+"; "+HeaderConfig.AUTH_KEY+": YOUR_TOKEN_KEY");
                        cmdModel.setRequest("["+new Command().toString()+"]");
                        cmdModel.setResponse("["+new Command().toString()+"]");
                        
                        mCommand.add(cmdModel);
                        break;
                    case FILTER_COMMAND:
                        cmdModel.setCommand(CmdConfig.FILTER_COMMAND.toString());
                        cmdModel.setCommandDescr("This command is used to filter application commands");
                        cmdModel.setExported(true);
                        cmdModel.setMethod("POST");
                        cmdModel.setUri("http://localhost:9001/Taxi24Dispatcher/dispatcher/process/fact");
                        cmdModel.setRequestHeader(HeaderConfig.CMD+":"+CmdConfig.FILTER_COMMAND.toString()+"; Content-Type:"+MediaType.APPLICATION_JSON+"; "+HeaderConfig.AUTH_KEY+": YOUR_TOKEN_KEY");
                        cmdModel.setRequest(new Command().toString());
                        cmdModel.setResponse("["+new Command().toString()+"]");
                        
                        mCommand.add(cmdModel);
                        break;
                    case FILTER_COMPONENT:
                        cmdModel.setCommand(CmdConfig.FILTER_COMPONENT.toString());
                        cmdModel.setCommandDescr("This command is used to filter application Systemcomponents");
                        cmdModel.setExported(true);
                        cmdModel.setMethod("POST");
                        cmdModel.setUri("http://localhost:9001/Taxi24Dispatcher/dispatcher/process/fact");
                        cmdModel.setRequestHeader(HeaderConfig.CMD+":"+CmdConfig.FILTER_COMPONENT.toString()+"; Content-Type:"+MediaType.APPLICATION_JSON+"; "+HeaderConfig.AUTH_KEY+": YOUR_TOKEN_KEY");
                        cmdModel.setRequest(new SystemComponent().toString());
                        cmdModel.setResponse("["+new SystemComponent().toString()+"]");
                        
                        mCommand.add(cmdModel);
                        break;
                    case SAVE_TOKEN:
                        cmdModel.setCommand(CmdConfig.SAVE_TOKEN.toString());
                        cmdModel.setCommandDescr("This command is used to save in cache a generated JWT token.");
                        cmdModel.setExported(true);
                        cmdModel.setMethod("POST");
                        cmdModel.setUri("http://localhost:9001/Taxi24Dispatcher/dispatcher/process/fact");
                        cmdModel.setRequestHeader(HeaderConfig.CMD+":"+CmdConfig.SAVE_TOKEN.toString()+"; Content-Type:"+MediaType.APPLICATION_JSON);
                        cmdModel.setRequest("JWT token");
                        cmdModel.setResponse("JWT token");
                        
                        mCommand.add(cmdModel);
                        break;
                    default:
                        cmdModel.setCommand(cmd.toString());
                        cmdModel.setCommandDescr("This command is used to "+cmd.toString()+".");
                        cmdModel.setExported(true);
                        cmdModel.setMethod("POST");
                        cmdModel.setUri("http://localhost:9001/Taxi24Dispatcher/dispatcher/process/fact");
                        cmdModel.setRequestHeader(HeaderConfig.CMD+":"+cmd.toString()+"; Content-Type:"+MediaType.APPLICATION_JSON+"; "+HeaderConfig.AUTH_KEY+": YOUR_TOKEN_KEY");
                        cmdModel.setRequest("any");
                        cmdModel.setResponse("any");
                        
                        mCommand.add(cmdModel);
                }
            }
            
            Systemcomponent.setCommands(mCommand);
            mSystemComponent.add(Systemcomponent);
            return mSystemComponent;
        }
    }
}
