/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.dispatcher.facades;

import biz.galaxy.commons.utilities.ErrorGeneralException;
import com.dispatcher.config.StatusConfig;
import com.dispatcher.entities.Command;
import com.dispatcher.entities.SystemComponent;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.dispatcher.utilities.QueryWrapper;
import org.springframework.stereotype.Component;

/**
 *
 * @author Aubain
 */
@Component
public class CommonQueries{
    @Autowired
            GenericDao genericDao;
    
    public SystemComponent findSystem(String systemName)throws ErrorGeneralException, Exception{
        QueryWrapper queryWrapper = new QueryWrapper("select A from SystemComponent A where A.name = :name and A.status = :status Order by A.creationTime DESC");
        queryWrapper.setParameter("name", systemName)
                .setParameter("status", StatusConfig.ACTIVE);
        List<SystemComponent> mSystemComponent = genericDao.findWithNamedQuery(queryWrapper);
        if(mSystemComponent == null || mSystemComponent.isEmpty())
            return null;
        return mSystemComponent.get(0);
    }
    public List<Command> listSystemCommand(String systemId)throws ErrorGeneralException, Exception{
        QueryWrapper queryWrapper = new QueryWrapper("select A from Command A where A.systemId = :systemId and A.status = :status Order by A.creationTime DESC");
        queryWrapper.setParameter("systemId", systemId)
                .setParameter("status", StatusConfig.ACTIVE);
        return genericDao.findWithNamedQuery(queryWrapper);
    }
    
    public Command findSystemCommand(String command, String systemId)throws ErrorGeneralException, Exception{
        QueryWrapper queryWrapper = new QueryWrapper("select A from Command A where (A.command = :command and A.systemId = :systemId) and A.status = :status Order by A.creationTime DESC");
        queryWrapper.setParameter("command", command)
                .setParameter("systemId", systemId)
                .setParameter("status", StatusConfig.ACTIVE);
        List<Command> mCommand = genericDao.findWithNamedQuery(queryWrapper);
        if(mCommand == null || mCommand.isEmpty())
            return null;
        return mCommand.get(0);
    }
    
    public Command findCommand(String command)throws ErrorGeneralException, Exception{
        QueryWrapper queryWrapper = new QueryWrapper("select A from Command A where A.command = :command and A.status = :status Order by A.creationTime DESC");
        queryWrapper.setParameter("command", command)
                .setParameter("status", StatusConfig.ACTIVE);
        List<Command> mCommand = genericDao.findWithNamedQuery(queryWrapper);
        if(mCommand == null || mCommand.isEmpty())
            return null;
        return mCommand.get(0);
    }
    
    public boolean isCommandCreated(String command)throws ErrorGeneralException, Exception{
        return findCommand(command) != null;
    }
    
    public boolean isSystemCommandCreated(String command, String systemId)throws ErrorGeneralException, Exception{
        return findSystemCommand(command, systemId) != null;
    }
    
    public boolean isSystemComponentCreated(String systemName)throws ErrorGeneralException, Exception{
        return findSystem(systemName) != null;
    }
    
    public <T> List<T> filter(QueryWrapper queryWrapper)throws ErrorGeneralException, Exception{
        return genericDao.findWithNamedQuery(queryWrapper);
    }
}
