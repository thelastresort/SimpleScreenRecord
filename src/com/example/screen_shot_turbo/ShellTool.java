package com.example.screen_shot_turbo;



import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.util.Log;

/**
 * ShellUtils
 * <ul>
 * <strong>Check root</strong>
 * <li>{@link ShellTool#checkRootPermission()}</li>
 * </ul>
 * <ul>
 * <strong>Execte command</strong>
 * <li>{@link ShellTool#execCommand(String, boolean)}</li>
 * <li>{@link ShellTool#execCommand(String, boolean, boolean)}</li>
 * <li>{@link ShellTool#execCommand(List, boolean)}</li>
 * <li>{@link ShellTool#execCommand(List, boolean, boolean)}</li>
 * <li>{@link ShellTool#execCommand(String[], boolean)}</li>
 * <li>{@link ShellTool#execCommand(String[], boolean, boolean)}</li>
 * </ul>
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-5-16
 */
public class ShellTool {

    public static final String COMMAND_SU       = "su";
    public static final String COMMAND_SH       = "sh";
    public static final String COMMAND_EXIT     = "exit\n";
    public static final String COMMAND_LINE_END = "\n";
    
    private static String TAG = "ShellTool";

    
    /**
     * check whether has root permission
     * 
     * @return
     */
    public static boolean checkRootPermission() {
//        return execCommand("echo root", true, false, null).result == 0;
        return execRoot(true);
    }

    /**
     * execute shell command, default return result msg
     * 
     * @param command command
     * @param isRoot whether need to run with root
     * @return
     * @see ShellTool#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String command, boolean isRoot) {
        return execCommand(new String[] {command}, isRoot, true);
    }
    
    /**
     * execute shell command, default return result msg
     * 
     * @param command command
     * @param isRoot whether need to run with root
     * @return
     * @see ShellTool#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String command1, String command2, boolean isRoot) {
    	return execCommand(new String[] {command1, command2}, isRoot, true);
    }

    /**
     * execute shell commands, default return result msg
     * 
     * @param commands command list
     * @param isRoot whether need to run with root
     * @return
     * @see ShellTool#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(List<String> commands, boolean isRoot) {
        return execCommand(commands == null ? null : commands.toArray(new String[] {}), isRoot, true);
    }

    /**
     * execute shell commands, default return result msg
     * 
     * @param commands command array
     * @param isRoot whether need to run with root
     * @return
     * @see ShellTool#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot) {
        return execCommand(commands, isRoot, true);
    }

    /**
     * execute shell command
     * 
     * @param command command
     * @param isRoot whether need to run with root
     * @param isNeedResultMsg whether need result msg
     * @return
     * @see ShellTool#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String command, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(new String[] {command}, isRoot, isNeedResultMsg);
    }

    /**
     * execute shell commands
     * 
     * @param commands command list
     * @param isRoot whether need to run with root
     * @param isNeedResultMsg whether need result msg
     * @return
     * @see ShellTool#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(List<String> commands, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(commands == null ? null : commands.toArray(new String[] {}), isRoot, isNeedResultMsg);
    }

//    /**
//     * execute shell commands
//     * 
//     * @param commands command array
//     * @param isRoot whether need to run with root
//     * @param isNeedResultMsg whether need result msg
//     * @return <ul>
//     *         <li>if isNeedResultMsg is false, {@link CommandResult#successMsg} is null and
//     *         {@link CommandResult#errorMsg} is null.</li>
//     *         <li>if {@link CommandResult#result} is -1, there maybe some excepiton.</li>
//     *         </ul>
//     */
//    public static CommandResult execCommand(String[] commands, boolean isRoot, boolean isNeedResultMsg, IShellListener listener) {
//        int result = -1;
//        if (commands == null || commands.length == 0) {
//            return new CommandResult(result, null, null);
//        }
//
//        Process process = null;
//        BufferedReader successResult = null;
//        BufferedReader errorResult = null;
//        StringBuilder successMsg = null;
//        StringBuilder errorMsg = null;
//
//        DataOutputStream os = null;
//        try {
//        	process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
//            
//            os = new DataOutputStream(process.getOutputStream());
//            
//            for (String command : commands) {
//                if (command == null) {
//                    continue;
//                }
//                // donnot use os.writeBytes(commmand), avoid chinese charset error
//                os.write(command.getBytes());
//                os.writeBytes(COMMAND_LINE_END);
//                os.flush();
//            }
//            
//            
//            
//            
//            os.writeBytes(COMMAND_EXIT);
//            os.flush();
//            
//            result = process.waitFor();
//            
//            // get command result
//            if (isNeedResultMsg) {
//                successMsg = new StringBuilder();
//                errorMsg = new StringBuilder();
//                successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
//                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//                String s;
//                while ((s = successResult.readLine()) != null) {
//                    successMsg.append(s);
//                }
//                while ((s = errorResult.readLine()) != null) {
//                    errorMsg.append(s);
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (os != null) {
//                    os.close();
//                }
//                if (successResult != null) {
//                    successResult.close();
//                }
//                if (errorResult != null) {
//                    errorResult.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            if (process != null) {
//                process.destroy();
//            }
//        }
//        
//        
//        return new CommandResult(result, successMsg == null ? null : successMsg.toString(), errorMsg == null ? null
//                : errorMsg.toString());
//    }
    
    
    /**
     * execute shell commands
     * 
     * @param commands command array
     * @param isRoot whether need to run with root
     * @param isNeedResultMsg whether need result msg
     * @return <ul>
     *         <li>if isNeedResultMsg is false, {@link CommandResult#successMsg} is null and
     *         {@link CommandResult#errorMsg} is null.</li>
     *         <li>if {@link CommandResult#result} is -1, there maybe some excepiton.</li>
     *         </ul>
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot, boolean isNeedResultMsg) {
    	int result = -1;
    	if (commands == null || commands.length == 0) {
    		return new CommandResult(result, null, null);
    	}
    	
    	BufferedReader successResult = null;
    	BufferedReader errorResult = null;
    	StringBuilder successMsg = null;
    	StringBuilder errorMsg = null;
    	
    	try {
    		if(mRootProcess == null){
    			return new CommandResult(result, null, null);
    		}
    		
    		StringBuilder cmdBuilder = new StringBuilder();
    		for (String command : commands) {
    			if (command == null) {
    				continue;
    			}
    			cmdBuilder.append(command + "\n");
    		}
    		
    		cmdBuilder.append("echo $?");
//    		cmdBuilder.append(COMMAND_EXIT);
    		String resultStr = mRootProcess.exec(cmdBuilder.toString());
    		Log.i(TAG, cmdBuilder + " resultStr " + resultStr);
    		
    		if(resultStr != null && resultStr.equals("0")){
    			result = 0;
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}

//		if (mRootProcess != null && result != 0) {
//			mRootProcess.destroy();
//			mRootProcess = null;
//		}
    	
    	return new CommandResult(result, successMsg == null ? null : successMsg.toString(), errorMsg == null ? null
    			: errorMsg.toString());
    }
    
    
    public static RootShellProcess mRootProcess;
    
    
    /**
     * execute shell su
     * 
     */
    public static boolean execRoot(boolean isNeedResultMsg) {
    	boolean result = false;
    	try {
    		if(mRootProcess != null){
    			return true;
    		}
    		
    		mRootProcess = new RootShellProcess();
    		
    		String resultStr = mRootProcess.exec("echo $?");
    		
    		if(resultStr != null && resultStr.equals("0")){
    			result = true;
    		}
    		
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	if (mRootProcess != null && !result) {
			mRootProcess.destroy();
			mRootProcess = null;
		}
    	
    	return result;
    }
    
    
    

    /**
     * result of command
     * <ul>
     * <li>{@link CommandResult#result} means result of command, 0 means normal, else means error, same to excute in
     * linux shell</li>
     * <li>{@link CommandResult#successMsg} means success message of command result</li>
     * <li>{@link CommandResult#errorMsg} means error message of command result</li>
     * </ul>
     * 
     * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-5-16
     */
    public static class CommandResult {

        /** result of command **/
        public int    result;
        /** success message of command result **/
        public String successMsg;
        /** error message of command result **/
        public String errorMsg;

        public CommandResult(int result) {
            this.result = result;
        }

        public CommandResult(int result, String successMsg, String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }
    }
    
    /**
     * 销毁root进程
     * @description
     */
    public static void destroyRootProcess(){
    	new Thread(){
    		public void run(){
    			synchronized (ShellTool.class) {
        			if(mRootProcess != null) {
        				try {
            				mRootProcess.exec(COMMAND_EXIT);
            				mRootProcess.destroy();
            				mRootProcess = null;
						} catch (Exception e) {
							// TODO: handle exception
						}
        			}
				}
    		}
    	}.start();
    }
    
    
    /**
     * shell命令监听器
     * @Description 
     *
     * 版本号    作者    日期    相关修改
     *  1.0   white   2014-12-15  创建
     *
     * @author white
     * 2014-12-15   --  上午9:43:02 
     */
    public interface IShellListener{
    	public void onRootSuccess();
    }
    
    
    /**
     * 有Root权限的shellProcess
     * @Description 
     *
     * 版本号    作者    日期    相关修改
     *  1.0   white   2014-12-15  创建
     *
     * @author white
     * 2014-12-15   --  下午5:24:38
     */
    public static class RootShellProcess{
    	private DataInputStream dis;
    	private DataInputStream errDis;
    	private DataOutputStream dos;
    	private Process mProcess;
    	
    	public RootShellProcess(){
    		try {
				mProcess = Runtime.getRuntime().exec(COMMAND_SU);
				dos = new DataOutputStream(mProcess.getOutputStream());	
				dis = new DataInputStream(mProcess.getInputStream());	
//				errDis = new DataInputStream(mProcess.getErrorStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    	
    	public synchronized String exec(String cmd){
    		try {
    			if (null != dos) {
    				dos.writeBytes(cmd + "\n");
    				dos.flush();
//					Log.e("wxj", "write:"+cmd);
    				String line = dis.readLine();
//					Log.e("wxj", "read:"+line);
//					String errorLine = errDis.readLine();
//					Log.e("wxj", "read error:"+errorLine);
    				return line;
    			}
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("wxj", "exec cmd error:"+e);
			}
    		return null;
    	}
    	
    	public synchronized void destroy(){
    		if(mProcess != null){
    			try {
    				if(dos != null)
    					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
    			try {
    				if(dis != null)
    					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
    			try {
    				if(errDis != null)
    					errDis.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    			mProcess.destroy();
    			mProcess = null;
    		}
    	}
    }
    
    
}
