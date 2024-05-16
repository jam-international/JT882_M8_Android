package communication;

public class UploadItem
    {
        private String LoacalPN = null;
        private String RemotePN = null;
        public synchronized String getLoacalPN()
            {
                return LoacalPN;
            }
        public synchronized void setLoacalPN(String loacalPN)
            {
                LoacalPN = loacalPN;
            }
        public synchronized String getRemotePN()
            {
                return RemotePN;
            }
        public synchronized void setRemotePN(String remotePN)
            {
                RemotePN = remotePN;
            }
        public UploadItem(String remotePN, String loacalPN)
            {
                super();
                LoacalPN = loacalPN;
                RemotePN = remotePN;
            }
                
    }
