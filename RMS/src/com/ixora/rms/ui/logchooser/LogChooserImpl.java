/*
 * Created on 11-Jan-2004
 */
package com.ixora.rms.ui.logchooser;

import java.util.List;

import com.ixora.common.ComponentConfiguration;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.MRUList;
import com.ixora.common.utils.Utils;
import com.ixora.rms.logging.LogRepositoryInfo;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.logchooser.xml.LogChooserHandlerXML;

/**
 * @author Daniel Moraru
 */
public final class LogChooserImpl
	implements LogChooser {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(LogChooserImpl.class);

	/** ViewContainer */
	private RMSViewContainer vc;

	/**
	 * Constructor.
	 * @param vc
	 */
	public LogChooserImpl(RMSViewContainer vc) {
		super();
		this.vc = vc;
	}

    /**
     * @see com.ixora.rms.ui.logchooser.LogChooser#getLogInfoForRead()
     */
    public LogRepositoryInfo getLogInfoForRead() {
        // try first to use the last used repository
        List<LogRepositoryInfo> logs = null;
        try {
			logs = ConfigurationMgr.getList(
		        LogChooserComponent.NAME,
		        LogRepositoryInfo.class,
		        LogChooserConfigurationConstants.MOST_RECENTLY_USED);
        } catch(Exception e) {
           logger.error(e);
        }
        LogRepositoryInfo.Type type = null;
        LogRepositoryInfo last = null;
		if(!Utils.isEmptyCollection(logs)) {
		    last = logs.get(0);
		    type = last.getRepositoryType();
		} else {
		   // if not use the type that's marked as the default
		   type = LogRepositoryInfo.Type.xml;
		}
		switch(type) {
			case xml:
		    	LogChooserHandlerXML xml = new LogChooserHandlerXML(vc);
		    	LogRepositoryInfo ret = xml.getLogInfoForRead(last);
		    	if(ret != null) {
				    // before returning save repository info
				    setLastUsedLog(ret);
					return ret;
			}
			break;	
		}
		return null;
    }

    /**
     * @param ret
     */
    private void setLastUsedLog(LogRepositoryInfo ret) {
        try {
	        ComponentConfiguration conf = ConfigurationMgr.get(
		            LogChooserComponent.NAME);
		    if(conf == null) {
		        logger.error("Configuration missing for component " + LogChooserComponent.NAME);
		        return;
		    }
			List<LogRepositoryInfo>lst = conf.getList(
			        LogRepositoryInfo.class,
			        LogChooserConfigurationConstants.MOST_RECENTLY_USED);
			List<LogRepositoryInfo> mru = new MRUList<LogRepositoryInfo>(
			        conf.getInt(LogChooserConfigurationConstants.MOST_RECENTLY_USED_SIZE),
			        lst);
			mru.add(ret);
			conf.setList(
			        LogChooserConfigurationConstants.MOST_RECENTLY_USED,
			        mru);
		    conf.save();
		} catch(Exception e) {
			logger.error(e);
		}
    }

    /**
     * @see com.ixora.rms.ui.logchooser.LogChooser#getLogInfoForWrite()
     */
    public LogRepositoryInfo getLogInfoForWrite() {
        // try first to use the last used repository type
        List<LogRepositoryInfo> logs = null;
        try {
			logs = ConfigurationMgr.getList(
		        LogChooserComponent.NAME,
		        LogRepositoryInfo.class,
		        LogChooserConfigurationConstants.MOST_RECENTLY_USED);
        } catch(Exception e) {
           logger.error(e);
        }
        LogRepositoryInfo.Type type = null;
        LogRepositoryInfo last = null;
		if(!Utils.isEmptyCollection(logs)) {
		    last = (LogRepositoryInfo)logs.get(0);
		    type = last.getRepositoryType();
		} else {
		   // if not use the type that's marked as the default
		   type = LogRepositoryInfo.Type.xml;
		}
		switch(type) {
			case xml:
		    	LogChooserHandlerXML xml = new LogChooserHandlerXML(vc);
		    	LogRepositoryInfo ret = xml.getLogInfoForWrite(last);
		    	if(ret != null) {
				    // before returning save the repository info
				    setLastUsedLog(ret);
					return ret;
		    	}
			break;
		}
        return null;
    }
}
