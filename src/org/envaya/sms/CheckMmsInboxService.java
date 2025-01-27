
package org.envaya.sms;

import android.app.IntentService;
import android.content.Intent;
import java.util.List;

public class CheckMmsInboxService extends IntentService
{
    private App app;
    private MmsUtils mmsUtils;

    public CheckMmsInboxService(String name)
    {
        super(name);        
    }
    
    public CheckMmsInboxService()
    {
        this("CheckMmsInboxService");        
    }

    @Override
    public void onCreate() {
        super.onCreate();

        app = (App)this.getApplicationContext();
        
        mmsUtils = app.getMmsUtils();
    }        

    @Override
    protected void onHandleIntent(Intent intent)
    {            
        List<IncomingMms> messages = mmsUtils.getMessagesInInbox();
        for (IncomingMms mms : messages)
        {                        
            if (mmsUtils.isNewMms(mms))
            {
                app.log("New MMS id=" + mms.getId() + " in inbox");
                // prevent forwarding MMS messages that existed in inbox
                // before EnvayaSMS started, or re-forwarding MMS multiple 
                // times if we don't delete them.
                mmsUtils.markOldMms(mms);                 

                if (mms.isForwardable())
                {                
                    app.inbox.forwardMessage(mms);                                    
                }
                else
                {
                    app.log("Ignoring incoming MMS from " + mms.getFrom());
                }
            }
        }
    }
}