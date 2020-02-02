package osm.jp.gpx.matchtime.gui;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;

public class Command extends Thread {
    String[] args;		// コマンドパラメータ
    private String commandName = "";	// コマンド名
    @SuppressWarnings({ "rawtypes" })
    private final Class cmd;		// 実行対象インスタンス

    /**
     * コンストラクタ：実行対象のインスタンスを得る
     * @param cmd
     */
    public Command(Class<?> cmd) {
        super();
        this.cmd = cmd;
        this.commandName = cmd.getName();
        this.args = new String[0];
    }

    /**
     * コマンドパラメータの設定
     * @param args
     */
    public void setArgs(String[] args) {
        this.args = args;
    }

    public void setCommandName(String name) {
        this.commandName = name;
    }
    public String getCommandName() {
        return this.commandName;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        System.out.println("[START:"+ (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss")).format(new java.util.Date()) +"]\t"+ this.commandName);
        for (int i=0; i < args.length; i++) {
            System.out.println(" args["+ i +"]: "+ this.args[i]);
        }
        System.out.println();

        try {
            try {
                java.lang.reflect.Method method = this.cmd.getMethod("main", new Class[] {String[].class});
                method.setAccessible(true);
                method.invoke(null, new Object[]{this.args});

                System.out.println();
                System.out.println("[END:"+ (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss")).format(new java.util.Date()) +"]\t"+ this.commandName);
            }
            catch (InvocationTargetException e) {
                System.out.println("[ERR!:"+ (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss")).format(new java.util.Date()) +"]\t"+ this.commandName);
                throw e;
            }
            catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException e) {
                System.out.println("[ERR!:"+ (new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss")).format(new java.util.Date()) +"]\t"+ this.commandName);
                throw e;
            }
        }
        catch(InvocationTargetException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace(System.out);
        }
        System.out.println();
    }
}
