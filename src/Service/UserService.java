package Service;

import Util.OnlineUtil;

public class UserService  {

    public boolean login(String username) {
        try {
            String msg = "uno01 login " + username + "\r\n";
            synchronized (OnlineUtil.messageLock) {
                OnlineUtil.sendMsg(msg);
                OnlineUtil.messageLock.wait(); // 等待服务器返回结果
                if (OnlineUtil.getUsername() == null)
                    return false;
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("UserService: login failed");
            return false;
        }
        return true;
    }
}
