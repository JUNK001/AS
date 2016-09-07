package cn.program.astudio.as;

/**
 * Created by CC on 2016/6/17.
 */
public class AccountManager {

    private final static Object syncObject=new Object();
    private static AccountManager accountManager;

    private AccountManager(boolean isfirst){
        if(isfirst)firstinit();
        else init();
    }

    public static AccountManager getInstance(boolean isfirst){
        if(accountManager==null){
            synchronized (syncObject){
                if(accountManager==null){
                    accountManager=new AccountManager(isfirst);
                }
            }
        }
        return accountManager;
    }

    public void firstinit(){

    }

    public void init(){

    }
}
