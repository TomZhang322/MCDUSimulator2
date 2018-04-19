package com.adcc.mcdu.simulator;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.adcc.mcdu.simulator.view.MainShell;
import com.adcc.utility.log.Log;

public class App {
    public static void main( String[] args ){
    	try {
			PropertyConfigurator.configure("./conf/log4j.properties");
			Display display = Display.getDefault();
			MainShell shell = new MainShell(display);					
			
			shell.open();
			shell.layout();
			
	        shell.addShellListener(new ShellAdapter() {
	            @Override
	            public void shellClosed(ShellEvent e) {
	                MessageBox mb = new MessageBox(shell,SWT.ICON_QUESTION | SWT.OK| SWT.CANCEL);
	                mb.setText("提示");
	                mb.setMessage("确定要关闭吗?");
	                if (e.doit = mb.open() == SWT.OK) {
	                    if (shell.mqTransfer != null) {
	                        // 断开连接
	                    	shell.mqTransfer.stopAsync();
	                    }
	                    if (shell.pool != null) {
	                        // 释放MQ连接池
	                    	shell.pool.dispose();
	                    }
	                }
	            }
	        });
			
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception ex) {
			Log.error("运行主面板main方法失败", ex);
		}
    }
}
