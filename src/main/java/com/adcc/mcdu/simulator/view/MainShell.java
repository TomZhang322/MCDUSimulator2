package com.adcc.mcdu.simulator.view;

import com.adcc.mcdu.simulator.entity.ACARS620Exception;
import com.adcc.mcdu.simulator.entity.ACARS620Msg;
import com.adcc.mcdu.simulator.ibmmq.IBMMQFactory;
import com.adcc.mcdu.simulator.util.ACARS620Parser;
import com.adcc.mcdu.simulator.util.AppConfiguration;
import com.adcc.mcdu.simulator.util.MCDUMsgParse;
import com.adcc.utility.log.Log;
import com.adcc.utility.mq.configuration.MQConfiguration;
import com.adcc.utility.mq.configuration.MQConfigurationFactory;
import com.adcc.utility.mq.entity.MQState;
import com.adcc.utility.mq.entity.Message;
import com.adcc.utility.mq.transfer.MQConnectionPool;
import com.adcc.utility.mq.transfer.MQConnectionPoolFactory;
import com.adcc.utility.mq.transfer.MQStateListener;
import com.adcc.utility.mq.transfer.MQTransfer;
import com.adcc.utility.mq.transfer.QueueMsgListener;
import com.adcc.utility.mq.transfer.ibm.IBMMQTransfer;
import com.adcc.utility.time.Time;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.adcc.utility.mq.transfer.ibm.AsyncQueueReceiver;
import com.adcc.utility.mq.transfer.ibm.IBMMQConnectionPool;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.*;
import org.eclipse.wb.swt.SWTResourceManager;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainShell extends Shell implements MQStateListener, QueueMsgListener{
	public static MainShell mainShell;
	private Menu menu;
	private Menu menuFile;
	private MenuItem menuItemFile;
	private MenuItem menuItemExit;

	private Group grpIBM;
	private Group grpConfigIBM;
	private Label lblHostIBM;
	private Text txtHostIBM;
	private Label lblPortIBM;
	private Text txtPortIBM;
	private Label lblQMIBM;
	private Text txtQMIBM;
	private Label lblCHIBM;
	private Text txtCHIBM;
	private Label lblQSendIBM;
	private Text txtQSendIBM;
	private Label lblQRecvIBM;
	private Text txtQRecvIBM;
	private Label lblIntervalIBM;
	private Text txtIntervalIBM;
	private Label lblExpiredIBM;
	private Text txtExpiredIBM;
	private Button btnConnectIBM;
	private Button btnSendIBM;
	//添加单发按钮
	private Button btnSendIBMOnce;
	private Button btnCancelIBM;
	private Button btnDisconnectIBM;
	private Group grpStateIBM;
	private Label lblStateIBM;
	private Composite cmpStateIBM;
	private Label lblSendConutIBM;
	private Label lblRecvCountIBM;
	private Text txtSendCountIBM;
	private Text txtRecvCountIBM;
	private Group grpSendIBM;
	private Text txtSendIBM;
	private Group grpRecvIBM;
	private Text txtRecvIBM;
	private Button clearReceiveCountButtonIBM;
	private Button clearSendCountButtonIBM;

	// IBMMQ
	private long txtSendNumIBM;

	private long txtRecvNumIBM;

	private long txtMASSendNum;

	private ScheduledExecutorService serviceIBM;

	private IBMMQFactory ibmmqFactory = AppConfiguration.getInstance().getIbmmqFactory();

	// MQ状态
	private MQState mqState = MQState.DISCONNECTED;

	// MQTransfer
	public MQTransfer mqTransfer = new IBMMQTransfer();

	// configuration
	public MQConfiguration configuration;

	// pool
	public MQConnectionPool pool;

	private List<String> msgList = new ArrayList<>();

	// RDI请求计数（满99重新置为01）
	private int msg_num = 1;
	private Group group_conditionConfig;
	private Label label;
	private Text text_airport;
	private Label label_1;
	private Text text_fi;
	private Label label_2;
	private Text text_an;
	private Label label_3;
	private Combo combo_type;
	private Button btnRadioButton_conditionConfig;
	private Button btnRadioButton_conditionFile;
	private Group group_file;
	private Button btnNewButton_chooseFile;
	private Button button_cleanSend;
	private Button button_cleanRecv;
	private Text textMASSendCount;
	private Label lblNewLabel_2;

	/**
	 * Create the shell.
	 * @param display
	 */
	public MainShell(Display display) {
		super(display, SWT.SHELL_TRIM);
		setSize(855, 871);
		mainShell = this;
		try {
//			mainShell = new Shell(SWT.DIALOG_TRIM | SWT.CLOSE | SWT.RESIZE);
			mainShell.setSize(855, 870);
			mainShell.setText("MCDU模拟器");
			int x = (Display.getDefault().getBounds().width - mainShell.getBounds().width)/2;
			int y = (Display.getDefault().getBounds().height - mainShell.getBounds().height)/2;
			mainShell.setLocation(x, y);

			menu = new Menu(mainShell, SWT.BAR);
			mainShell.setMenuBar(menu);

			menuItemFile = new MenuItem(menu, SWT.CASCADE);
			menuItemFile.setText("文件");

			menuFile = new Menu(mainShell,SWT.DROP_DOWN);
			menuItemFile.setMenu(menuFile);

			menuItemExit = new MenuItem(menuFile, SWT.CASCADE);
			menuItemExit.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					menuItemExitClick(e);
				}
			});
			menuItemExit.setText("退出");

			// IBMMQ
			grpIBM = new Group(mainShell, SWT.NONE);
			grpIBM.setText("IBMMQ");
			grpIBM.setBounds(10, 10, 819, 802);

			grpSendIBM = new Group(grpIBM, SWT.NONE);
			grpSendIBM.setText("发送");
			grpSendIBM.setBounds(268, 320, 533, 219);

			txtSendIBM = new Text(grpSendIBM, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI | SWT.READ_ONLY);
			txtSendIBM.setBounds(10, 30, 513, 186);

			grpConfigIBM = new Group(grpIBM, SWT.NONE);
			grpConfigIBM.setText("配置");
			grpConfigIBM.setBounds(10, 20, 235, 485);

			lblHostIBM = new Label(grpConfigIBM, SWT.NONE);
			lblHostIBM.setBounds(10, 25, 40, 25);
			lblHostIBM.setText("HOST:");

			txtHostIBM = new Text(grpConfigIBM, SWT.BORDER);
			txtHostIBM.setText(ibmmqFactory.getAmqsFactoryList().get(0).getHost());
			txtHostIBM.setBounds(69, 22, 153, 23);

			lblPortIBM = new Label(grpConfigIBM, SWT.NONE);
			lblPortIBM.setBounds(10, 62, 40, 25);
			lblPortIBM.setText("PORT:");

			txtPortIBM = new Text(grpConfigIBM, SWT.BORDER);
			txtPortIBM.setBounds(69, 62, 153, 23);
			txtPortIBM.setText(String.valueOf(ibmmqFactory.getAmqsFactoryList().get(0).getPort()));

			lblQMIBM = new Label(grpConfigIBM, SWT.NONE);
			lblQMIBM.setBounds(10, 99, 25, 25);
			lblQMIBM.setText("QM:");

			txtQMIBM = new Text(grpConfigIBM, SWT.BORDER);
			txtQMIBM.setBounds(69, 99, 153, 23);
			txtQMIBM.setText(ibmmqFactory.getAmqsFactoryList().get(0).getQueueManager());

			lblCHIBM = new Label(grpConfigIBM, SWT.NONE);
			lblCHIBM.setBounds(10, 136, 53, 25);
			lblCHIBM.setText("CHL:");

			txtCHIBM = new Text(grpConfigIBM, SWT.BORDER);
			txtCHIBM.setBounds(69, 136, 153, 23);
			txtCHIBM.setText(ibmmqFactory.getAmqsFactoryList().get(0).getChannel());

			lblQSendIBM = new Label(grpConfigIBM, SWT.NONE);
			lblQSendIBM.setBounds(10, 173, 51, 17);
			lblQSendIBM.setText("发送队列:");

			txtQSendIBM = new Text(grpConfigIBM, SWT.BORDER);
			txtQSendIBM.setBounds(69, 173, 153, 23);
			txtQSendIBM.setText(ibmmqFactory.getAmqsFactoryList().get(0).getSendQueue());

			lblQRecvIBM = new Label(grpConfigIBM, SWT.NONE);
			lblQRecvIBM.setBounds(10, 210, 51, 17);
			lblQRecvIBM.setText("接收队列:");

			txtQRecvIBM = new Text(grpConfigIBM, SWT.BORDER);
			txtQRecvIBM.setBounds(69, 210, 153, 23);
			txtQRecvIBM.setText(ibmmqFactory.getAmqsFactoryList().get(0).getRecvQueue());

			lblIntervalIBM = new Label(grpConfigIBM, SWT.NONE);
			lblIntervalIBM.setBounds(12, 247, 51, 17);
			lblIntervalIBM.setText("发送间隔:");

			txtIntervalIBM = new Text(grpConfigIBM, SWT.BORDER);
			txtIntervalIBM.setBounds(69, 247, 45, 23);
			txtIntervalIBM.setText("50");

			lblExpiredIBM = new Label(grpConfigIBM, SWT.NONE);
			lblExpiredIBM.setBounds(120, 247, 51, 17);
			lblExpiredIBM.setText("超时时间:");

			txtExpiredIBM = new Text(grpConfigIBM, SWT.BORDER);
			txtExpiredIBM.setBounds(177, 247, 45, 23);
			txtExpiredIBM.setText(String.valueOf(ibmmqFactory.getTimeout()));

			btnConnectIBM = new Button(grpConfigIBM, SWT.NONE);
			btnConnectIBM.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					btnIBMConnectExitClick(e);
				}
			});
			btnConnectIBM.setBounds(26, 285, 60, 27);
			btnConnectIBM.setText("连接");

			btnSendIBM = new Button(grpConfigIBM, SWT.NONE);
			btnSendIBM.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					btnIBMSend(e);
				}
			});
			btnSendIBM.setBounds(92, 285, 60, 27);
			btnSendIBM.setText("连续发送");
			btnSendIBM.setEnabled(false);

			btnSendIBMOnce = new Button(grpConfigIBM,SWT.NONE);
			btnSendIBMOnce.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					btnIBMSendOnce(e);
				}
			});
			btnSendIBMOnce.setBounds(162, 285, 60, 27);
			btnSendIBMOnce.setText("发送");
			btnSendIBMOnce.setEnabled(false);

			btnCancelIBM = new Button(grpConfigIBM, SWT.NONE);
			btnCancelIBM.setBounds(92, 317, 60, 27);
			btnCancelIBM.setText("取消发送");
			btnCancelIBM.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					shutdownTimerServiceIBM(e);
				}
			});
			btnCancelIBM.setEnabled(false);

			btnDisconnectIBM = new Button(grpConfigIBM, SWT.NONE);
			btnDisconnectIBM.setBounds(26, 317, 60, 27);
			btnDisconnectIBM.setText("断开");
			btnDisconnectIBM.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					btnIBMDisconnect(e);
				}
			});
			btnDisconnectIBM.setEnabled(false);

			lblNewLabel_2 = new Label(grpConfigIBM, SWT.NONE);
			lblNewLabel_2.setForeground(SWTResourceManager.getColor(SWT.COLOR_RED));
			lblNewLabel_2.setBounds(10, 379, 215, 17);
			lblNewLabel_2.setText("注意：发送频率控制在1秒最多5份！！！");

			grpStateIBM = new Group(grpIBM, SWT.NONE);
			grpStateIBM.setText("状态及统计");
			grpStateIBM.setBounds(10, 511, 235, 281);

			lblStateIBM = new Label(grpStateIBM, SWT.NONE);
			lblStateIBM.setBounds(12, 26, 51, 17);
			lblStateIBM.setText("连接状态:");

			cmpStateIBM = new Composite(grpStateIBM, SWT.NONE);
			cmpStateIBM.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
			cmpStateIBM.setBounds(69, 24, 153, 23);

			lblSendConutIBM = new Label(grpStateIBM, SWT.NONE);
			lblSendConutIBM.setBounds(12, 66, 51, 17);
			lblSendConutIBM.setText("发送计数:");

			txtSendCountIBM = new Text(grpStateIBM, SWT.BORDER);
			txtSendCountIBM.setBounds(69, 62, 153, 23);

			lblRecvCountIBM = new Label(grpStateIBM, SWT.NONE);
			lblRecvCountIBM.setBounds(12, 143, 51, 17);
			lblRecvCountIBM.setText("接收计数:");

			txtRecvCountIBM = new Text(grpStateIBM, SWT.BORDER);
			txtRecvCountIBM.setBounds(69, 140, 153, 23);

			clearSendCountButtonIBM = new Button(grpStateIBM, SWT.NONE);
			clearSendCountButtonIBM.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					txtSendNumIBM = 0;
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							txtSendCountIBM.setText(String.valueOf(txtSendNumIBM));
						}
					});
				}
			});
			clearSendCountButtonIBM.setBounds(22, 180, 85, 27);
			clearSendCountButtonIBM.setText("清空发送计数");

			clearReceiveCountButtonIBM = new Button(grpStateIBM, SWT.NONE);
			clearReceiveCountButtonIBM.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					txtRecvNumIBM = 0;
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							txtRecvCountIBM.setText(String.valueOf(txtRecvNumIBM));
						}
					});
				}
			});
			clearReceiveCountButtonIBM.setBounds(125, 180, 85, 27);
			clearReceiveCountButtonIBM.setText("清空接收计数");

			button_cleanSend = new Button(grpStateIBM, SWT.NONE);
			button_cleanSend.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					txtSendIBM.setText("");
				}
			});
			button_cleanSend.setBounds(22, 244, 85, 27);
			button_cleanSend.setText("清空发送内容");

			button_cleanRecv = new Button(grpStateIBM, SWT.NONE);
			button_cleanRecv.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					txtRecvIBM.setText("");
				}
			});
			button_cleanRecv.setText("清空接收内容");
			button_cleanRecv.setBounds(125, 244, 85, 27);

			Label lblNewLabel_1 = new Label(grpStateIBM, SWT.NONE);
			lblNewLabel_1.setBounds(10, 104, 61, 17);
			lblNewLabel_1.setText("MAS回复:");

			textMASSendCount = new Text(grpStateIBM, SWT.BORDER);
			textMASSendCount.setBounds(69, 98, 153, 23);

			Button btnClearMASCount = new Button(grpStateIBM, SWT.NONE);
			btnClearMASCount.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					txtMASSendNum = 0;
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							textMASSendCount.setText(String.valueOf(txtMASSendNum));
						}
					});
				}
			});
			btnClearMASCount.setBounds(22, 211, 85, 27);
			btnClearMASCount.setText("清空MAS回复");

			grpRecvIBM = new Group(grpIBM, SWT.NONE);
			grpRecvIBM.setText("接收");
			grpRecvIBM.setBounds(268, 545, 533, 247);

			txtRecvIBM = new Text(grpRecvIBM, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL | SWT.MULTI | SWT.WRAP);
			txtRecvIBM.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			txtRecvIBM.setBounds(10, 34, 513, 213);

			Group grpDai = new Group(grpIBM, SWT.NONE);
			grpDai.setText("DAI请求配置");
			grpDai.setBounds(268, 20, 533, 294);

			btnRadioButton_conditionConfig = new Button(grpDai, SWT.RADIO);
			btnRadioButton_conditionConfig.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (btnRadioButton_conditionConfig.getSelection()) {
						group_conditionConfig.setEnabled(true);
						group_conditionConfig.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
						group_file.setEnabled(false);
						group_file.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
					}
				}
			});
			btnRadioButton_conditionConfig.setSelection(true);
			btnRadioButton_conditionConfig.setBounds(27, 27, 97, 17);
			btnRadioButton_conditionConfig.setText("条件发送模式");

			btnRadioButton_conditionFile = new Button(grpDai, SWT.RADIO);
			btnRadioButton_conditionFile.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (btnRadioButton_conditionFile.getSelection()) {
						group_conditionConfig.setEnabled(false);
						group_conditionConfig.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
						group_file.setEnabled(true);
						group_file.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
					}
				}
			});
			btnRadioButton_conditionFile.setBounds(140, 27, 97, 17);
			btnRadioButton_conditionFile.setText("文件发送模式");

			group_conditionConfig = new Group(grpDai, SWT.NONE);
			group_conditionConfig.setText("条件发送模式配置");
			group_conditionConfig.setBounds(27, 55, 474, 151);

			label = new Label(group_conditionConfig, SWT.NONE);
			label.setText("机场四码：");
			label.setBounds(35, 30, 61, 17);

			text_airport = new Text(group_conditionConfig, SWT.BORDER);
			text_airport.setBounds(111, 27, 88, 23);

			label_1 = new Label(group_conditionConfig, SWT.NONE);
			label_1.setText("航班号：");
			label_1.setBounds(238, 27, 61, 17);

			text_fi = new Text(group_conditionConfig, SWT.BORDER);
			text_fi.setBounds(330, 27, 88, 23);

			label_2 = new Label(group_conditionConfig, SWT.NONE);
			label_2.setText("机尾号：");
			label_2.setBounds(35, 71, 61, 17);

			text_an = new Text(group_conditionConfig, SWT.BORDER);
			text_an.setBounds(111, 68, 88, 23);

			label_3 = new Label(group_conditionConfig, SWT.NONE);
			label_3.setText("RAI请求类型：");
			label_3.setBounds(238, 66, 80, 17);

			combo_type = new Combo(group_conditionConfig, SWT.NONE);
			combo_type.setToolTipText("");
			combo_type.setItems(new String[] {"A", "C", "D", "T"});
			combo_type.setBounds(330, 63, 88, 25);
			combo_type.select(0);

			group_file = new Group(grpDai, SWT.NONE);
			group_file.setText("文件发送模式选择");
			group_file.setBounds(27, 219, 474, 65);
			group_file.setEnabled(false);
			group_file.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));

			btnNewButton_chooseFile = new Button(group_file, SWT.NONE);
			btnNewButton_chooseFile.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					msgList = readFile();
				}
			});
			btnNewButton_chooseFile.setBounds(31, 26, 77, 29);
			btnNewButton_chooseFile.setText("选择txt文件");

			Label label_4 = new Label(grpIBM, SWT.SEPARATOR | SWT.VERTICAL);
			label_4.setBounds(251, 0, 11, 792);
		} catch (Exception ex) {
			Log.error("面板加载数据报错", ex);
		}

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	@Override
	public void onState(MQState state, Map<String, String> mqInfo) {
		try {
			Log.info("mqInfo:" + mqInfo + " state:" + state);
			if (state == MQState.CONNECTED) {
				mqState = MQState.CONNECTED;
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						// 这段代码实际上会被放在UI线程中执行
						cmpStateIBM.setBackground(SWTResourceManager.getColor(SWT.COLOR_GREEN));
						btnConnectIBM.setEnabled(false);
						btnSendIBM.setEnabled(true);
						btnSendIBMOnce.setEnabled(true);
						btnCancelIBM.setEnabled(false);
						btnDisconnectIBM.setEnabled(true);

					}
				});
			} else if(state == MQState.CONNECTING) {
				mqState = MQState.CONNECTING;//异常时状态
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						// 这段代码实际上会被放在UI线程中执行
						cmpStateIBM.setBackground(SWTResourceManager.getColor(SWT.COLOR_YELLOW));
						btnConnectIBM.setEnabled(false);
						btnSendIBM.setEnabled(false);
						btnSendIBMOnce.setEnabled(false);
						btnCancelIBM.setEnabled(false);
						btnDisconnectIBM.setEnabled(false);
					}
				});
			} else {
				mqState = MQState.DISCONNECTED;//手动断开时状态
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						// 这段代码实际上会被放在UI线程中执行
						cmpStateIBM.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
						btnConnectIBM.setEnabled(true);
						btnSendIBM.setEnabled(false);
						btnSendIBMOnce.setEnabled(false);
						btnCancelIBM.setEnabled(false);
						btnDisconnectIBM.setEnabled(false);
					}
				});
			}
		} catch (Exception ex) {
			Log.error("onState() error", ex);
		}
	}

	@Override
	public void onQueueMsg(String name, Message message, Map<String, String> mqInfo) {
		String strMsg = new String(message.toString());
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				txtRecvIBM.append("\n\r" + strMsg);
				System.out.println("txtRecvIBM:" + txtRecvIBM);
				txtRecvCountIBM.setText(String.valueOf(++txtRecvNumIBM));
			}
		});
		System.out.println("NAME:" + name + " MESSAGE:"
				+ "\nhead:" + message.getHead()
				+ "\nmessage:" + message);
		try {
			// 根据条件回复MAS报文
			String masRuquestFlag = AppConfiguration.getInstance().getMasRuquestFlag();
			if (message.getContent() == null) {
				return;
			}
			String content = new String(message.getContent());
			//解析成ACARS620Msg
			Optional<ACARS620Msg> opt = ACARS620Parser.parse(content);
			if (opt.isPresent()) {
				ACARS620Msg acars620Msg = opt.get();
				String ma = acars620Msg.getMa();
				// MA有值才回复MAS报文
				if (!Strings.isNullOrEmpty(ma)) {
					String fi = acars620Msg.getFi();
					String an = acars620Msg.getAn();
					String masResponseMsg = assembleMasMsg(fi, an, ma, masRuquestFlag);
					try {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								// 定义发送任务
								Message queueMsg = new Message(new String(masResponseMsg).getBytes());
								queueMsg.getHead().put("TYPE", "MCDU");
								queueMsg.getHead().put("LENGTH", queueMsg.getLength());
								try {
									((IBMMQTransfer)mqTransfer).sendQueue(txtQSendIBM.getText().trim(), queueMsg, 3000);
									Display.getDefault().syncExec(new Runnable() {
										public void run() {
											//发送内容显示
											txtSendIBM.append("\n" + queueMsg);
											textMASSendCount.setText(String.valueOf(++txtMASSendNum));
											System.out.println("NAME:" + txtQSendIBM.getText().trim() + " MESSAGE:"
													+ "\nhead:" + queueMsg.getHead()
													+ "\nmessage:" + queueMsg);
										}
									});
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		} catch (ACARS620Exception e) {
			Log.error("send MAS failed!", e);
		}
	}

	private void btnIBMConnectExitClick(SelectionEvent e) {

		try {
			// 设置MQ连接参数
			configuration = MQConfigurationFactory.getInstance().createIBMMQConfiguration(txtHostIBM.getText().trim(),
					Integer.valueOf(txtPortIBM.getText().trim()),txtQMIBM.getText().trim(), txtCHIBM.getText().trim());
			mqTransfer.setConfiguration(configuration);
			// 设置MQ连接池参数
			pool = MQConnectionPoolFactory.getInstance().createIBMMQConnectionPool();
			((IBMMQConnectionPool)pool).setActiveMode(ibmmqFactory.getActiveMode());
			((IBMMQConnectionPool)pool).setTimeout(ibmmqFactory.getTimeout());
			((IBMMQConnectionPool)pool).setMaxConnections(ibmmqFactory.getMaxConnections());
			((IBMMQConnectionPool)pool).setMaxIdelConnections(ibmmqFactory.getMaxIdelConnections());
			mqTransfer.setConnectionPool(pool);
			pool.init(configuration);
			// 设置MQ监听器
			mqTransfer.setMQStateListener(this);
			mqTransfer.getAsyncQReceivers().add(new AsyncQueueReceiver(txtQRecvIBM.getText().trim()));
			mqTransfer.setQueueListener(this);
			System.out.println("test recvQueue:"+txtQRecvIBM.getText().trim());
			mqTransfer.startAsync();

		} catch (Exception ex) {
			Log.error("build() error", ex);
			cmpStateIBM.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
			btnConnectIBM.setEnabled(true);
			btnSendIBM.setEnabled(false);
			btnSendIBMOnce.setEnabled(false);
			btnCancelIBM.setEnabled(false);
			btnDisconnectIBM.setEnabled(false);
		}
	}

	private void btnIBMSend(SelectionEvent e) {
		try {
//            if(ibmqm == null || !ibmqm.isConnected()) {
//                return;
//            }

			btnSendIBM.setEnabled(false);
			btnCancelIBM.setEnabled(true);

			// 定义发送任务
			String expiredIBM = txtExpiredIBM.getText().trim();
			String QSendIBM = txtQSendIBM.getText().trim();
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								if (btnRadioButton_conditionConfig.getSelection() && !btnRadioButton_conditionFile.getSelection()) {
									try {
										// 条件发送模式
										String code4 = text_airport.getText().trim();
										String fi = text_fi.getText().trim();
										String an = text_an.getText().trim();
										String type = combo_type.getText().trim();
										//取当前时间
//										Date date = new Date();
										Date date = Time.getCurUTCDate();
										int day = date.getDate();
										int hours = date.getHours();
										int minutes = date.getMinutes();
										String MCDUMsg = assembleMCDUMsg(code4, fi, an, type, day, hours, minutes);
										Message queueMsg = new Message(new String(MCDUMsg).getBytes());
										queueMsg.getHead().put("TYPE", "MCDU");
										queueMsg.getHead().put("LENGTH", queueMsg.getLength());
										((IBMMQTransfer)mqTransfer).sendQueue(QSendIBM, queueMsg, Integer.parseInt(expiredIBM));
										Display.getDefault().syncExec(new Runnable() {
											public void run() {
												//发送内容显示
												txtSendIBM.append("\n" + queueMsg);
												txtSendCountIBM.setText(String.valueOf(++txtSendNumIBM));
												System.out.println("NAME:" + txtQSendIBM.getText().trim() + " MESSAGE:"
														+ "\nhead:" + queueMsg.getHead()
														+ "\nmessage:" + queueMsg);
											}
										});
									}catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									// 文件发送模式
									if (msgList != null && msgList.size() > 0) {
										for (String MCDUMsg : msgList) {
											try {
												Message queueMsg = new Message(new String(MCDUMsg).getBytes());
												queueMsg.getHead().put("TYPE", "MCDU");
												queueMsg.getHead().put("LENGTH", queueMsg.getLength());
												((IBMMQTransfer)mqTransfer).sendQueue(QSendIBM, queueMsg, Integer.parseInt(expiredIBM));
												Display.getDefault().syncExec(new Runnable() {
													public void run() {
														//发送内容显示
														txtSendIBM.append("\n" + queueMsg);
														txtSendCountIBM.setText(String.valueOf(++txtSendNumIBM));
														System.out.println("NAME:" + txtQSendIBM.getText().trim() + " MESSAGE:"
																+ "\nhead:" + queueMsg.getHead()
																+ "\nmessage:" + queueMsg);
													}
												});
											}catch (Exception e) {
												e.printStackTrace();
											}
										}
									} else {
										serviceIBM.shutdown();
										MessageBox mb = new MessageBox(mainShell, SWT.ICON_ERROR | SWT.OK);
										mb.setText("系统提示");
										mb.setMessage("没有报文！");
										mb.open();

									}
								}
							}
						});
					} catch (Exception e) {
						Log.error("send message failed!", e);
					}
				}
			};
			// 定时执行发报任务
			serviceIBM = Executors.newSingleThreadScheduledExecutor();
			serviceIBM.scheduleAtFixedRate(runnable, 0, Integer.parseInt(txtIntervalIBM.getText().trim()), TimeUnit.MILLISECONDS);
		} catch (Exception ex) {
			Log.error("btnIBMSend error()", ex);
		}
	}

	private void btnIBMSendOnce(SelectionEvent e) {
		try {
//            if(ibmqm == null || !ibmqm.isConnected()) {
//                return;
//            }

//			btnSendIBMOnce.setEnabled(false);
			btnCancelIBM.setEnabled(true);

			if (btnRadioButton_conditionConfig.getSelection() && !btnRadioButton_conditionFile.getSelection()) {
				// 条件发送模式
				String code4 = text_airport.getText().trim();
				String fi = text_fi.getText().trim();
				String an = text_an.getText().trim();
				String type = combo_type.getText().trim();
				//取当前时间
//				Date date = new Date();
				Date date = Time.getCurUTCDate();
				int day = date.getDate();
				int hours = date.getHours();
				int minutes = date.getMinutes();
				if (!Strings.isNullOrEmpty(code4) && !Strings.isNullOrEmpty(fi) && !Strings.isNullOrEmpty(an)
						&& !Strings.isNullOrEmpty(type)) {
					String MCDUMsg = assembleMCDUMsg(code4, fi, an, type, day, hours, minutes);
					// 定义发送任务
					Message queueMsg = new Message(new String(MCDUMsg).getBytes());
					queueMsg.getHead().put("TYPE", "MCDU");
					queueMsg.getHead().put("LENGTH", queueMsg.getLength());
					((IBMMQTransfer)mqTransfer).sendQueue(txtQSendIBM.getText().trim(), queueMsg, 3000);
					Display.getDefault().syncExec(new Runnable() {
						public void run() {
							//发送内容显示
							txtSendIBM.append("\n" + queueMsg);
							txtSendCountIBM.setText(String.valueOf(++txtSendNumIBM));
							System.out.println("NAME:" + txtQSendIBM.getText().trim() + " MESSAGE:"
									+ "\nhead:" + queueMsg.getHead()
									+ "\nmessage:" + queueMsg);
						}
					});
				} else {
					MessageBox messageBox = new MessageBox(mainShell, SWT.ICON_WARNING |SWT.OK);
					messageBox.setMessage("机场四码、航班号、机尾号等都不能为空！");
					messageBox.open();
				}
			} else {
				// 文件发送模式
				if (msgList != null && msgList.size() > 0) {
					for (String MCDUMsg : msgList) {
						// 定义发送任务
						Message queueMsg = new Message(new String(MCDUMsg).getBytes());
						queueMsg.getHead().put("TYPE", "MCDU");
						queueMsg.getHead().put("LENGTH", queueMsg.getLength());
						((IBMMQTransfer)mqTransfer).sendQueue(txtQSendIBM.getText().trim(), queueMsg, 3000);
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								//发送内容显示
								txtSendIBM.append("\n" + queueMsg);
								txtSendCountIBM.setText(String.valueOf(++txtSendNumIBM));
								System.out.println("NAME:" + txtQSendIBM.getText().trim() + " MESSAGE:"
										+ "\nhead:" + queueMsg.getHead()
										+ "\nmessage:" + queueMsg);
							}
						});
					}
				} else {
					MessageBox mb = new MessageBox(mainShell, SWT.ICON_ERROR | SWT.OK);
					mb.setText("系统提示");
					mb.setMessage("没有报文！");
					mb.open();
				}
			}
		} catch (Exception ex) {
			Log.error("btnIBMSend error()", ex);
		}
	}

	private void shutdownTimerServiceIBM(SelectionEvent e) {
		try {
			if (serviceIBM != null) {
				serviceIBM.shutdown();
			}
			btnSendIBM.setEnabled(true);
			btnCancelIBM.setEnabled(false);
			Log.info("shutdownTimerServiceIBM success");
		} catch (Exception ex) {
			Log.error("shutdownTimerServiceIBM error()", ex);
		}
	}

	private void btnIBMDisconnect(SelectionEvent e) {
		try {
			// 取消发送
			shutdownTimerServiceIBM(e);
			// 断开连接
			mqTransfer.stopAsync();
			// 释放MQ连接池
			pool.dispose();
			// 初始化界面显示
//            cmpStateIBM.setBackground(SWTResourceManager.getColor(SWT.COLOR_RED));
//            btnConnectIBM.setEnabled(true);
//            btnSendIBM.setEnabled(false);
//            btnSendIBMOnce.setEnabled(false);
//            btnCancelIBM.setEnabled(false);
//            btnDisconnectIBM.setEnabled(false);

			Log.info("btnIBMDisconnect success");
		} catch (Exception ex) {
			Log.error("btnIBMDisconnect() error", ex);
		}
	}

	private void menuItemExitClick(SelectionEvent e) {
		try{
			Display.getDefault().close();
		}catch(Exception ex){
			Log.error("menuItemExitClick() error", ex);
		}
	}

	private String assembleMasMsg(String fi, String an, String ma, String masRuquestFlag) {
		try {
			SimpleDateFormat ddHHmm = new SimpleDateFormat("ddHHmm");
			StringBuffer masSB = new StringBuffer("");

			masSB.append((char)1).append("QU ").append("BJSGWYA").append("\r\n");
			masSB.append(".").append("BJSXCXA ").append(ddHHmm.format(Time.getCurUTCDate())).append("\r\n");
			masSB.append((char) 2).append("MAS").append("\r\n");
			masSB.append("FI ").append(fi).append("/").append("AN ").append(an).append("/MA ").append(ma.substring(0, ma.length()-1)).append(masRuquestFlag).append("\r\n");

			if (msg_num >= 1 && msg_num <= 9) {
				masSB.append("DT BJS PEK ").append(ddHHmm.format(Time.getCurUTCDate())).append(" M0").append(String.valueOf(msg_num)).append("A").append("\r\n");
				msg_num++;
			} else if (msg_num > 9 && msg_num < 99) {
				masSB.append("DT BJS PEK ").append(ddHHmm.format(Time.getCurUTCDate())).append(" M").append(String.valueOf(msg_num)).append("A").append("\r\n");
				msg_num++;
			} else if (msg_num == 99) {
				masSB.append("DT BJS PEK ").append(ddHHmm.format(Time.getCurUTCDate())).append(" M").append("99").append("A").append("\r\n");
				msg_num = 1;
			}

			masSB.append((char) 3);

			return masSB.toString();
		} catch (Exception e) {
			Log.error("assembleMasMsg method error", e);
		}
		return null;
	}

	private String assembleMCDUMsg(String code4, String fi, String an, String type, int day, int hours, int minutes) {
		try {
			String strDay = "";
			String strHours = "";
			String strMinutes = "";
			if (day >= 1 && day <= 9) {
				strDay = "0" + String.valueOf(day);
			} else {
				strDay = String.valueOf(day);
			}
			if (hours >= 0 && hours <= 9) {
				strHours = "0" + String.valueOf(hours);
			} else {
				strHours = String.valueOf(hours);
			}
			if (minutes >= 0 && minutes <= 9) {
				strMinutes = "0" + String.valueOf(minutes);
			} else {
				strMinutes = String.valueOf(minutes);
			}

			StringBuffer mcduSB = new StringBuffer("");

			mcduSB.append((char)1).append("QU ").append("ATSXCXA ").append("HDQBQSK").append("\r\n");
			mcduSB.append(".").append("BJSXCXA ").append(strDay + strHours + strMinutes).append("\r\n");
			mcduSB.append((char) 2).append("RAI").append("\r\n");
			mcduSB.append("FI ").append(fi).append("/").append("AN ").append(an).append("\r\n");

			if (msg_num >= 1 && msg_num <= 9) {
				mcduSB.append("DT BJS RGS ").append(strDay + strHours + strMinutes).append(" M0").append(String.valueOf(msg_num)).append("A").append("\r\n");
				msg_num++;
			} else if (msg_num > 9 && msg_num < 99) {
				mcduSB.append("DT BJS RGS ").append(strDay + strHours + strMinutes).append(" M").append(String.valueOf(msg_num)).append("A").append("\r\n");
				msg_num++;
			} else if (msg_num == 99) {
				mcduSB.append("DT BJS RGS ").append(strDay + strHours + strMinutes).append(" M").append("99").append("A").append("\r\n");
				msg_num = 1;
			}

			StringBuffer freeTextSB = new StringBuffer("");
			freeTextSB.append("TI2/022").append(code4).append(type);
			String freeText = freeTextSB.toString();

			mcduSB.append("-  ").append(freeText).append(MCDUMsgParse.getCrc(freeText)).append("\r\n");
			mcduSB.append((char) 3);
			return mcduSB.toString();
		} catch (Exception e) {
			Log.error("assembleMCDUMsg method error", e);
		}
		return null;
	}

	// 读取file
	private List<String> readFile() {
		try {
			FileDialog dialog = new FileDialog(mainShell, SWT.OPEN);
			dialog.setText("选择文件");
			dialog.setFilterPath("C:\\");
			dialog.setFilterExtensions(new String[]{"*.txt", "*.*"});
			dialog.setFilterNames(new String[]{"Text Files (*.txt)", "All Files (*.*)"});
			String file = dialog.open();

			if (Strings.isNullOrEmpty(file)) {
				MessageBox mb = new MessageBox(mainShell, SWT.ICON_ERROR | SWT.OK);
				mb.setText("系统提示");
				mb.setMessage("文件为空！");
				mb.open();
				return null;
			}

			List<String> msgList = findMsgListFromFile(file);

			if (msgList == null || msgList.size() <= 0) {
				MessageBox mb = new MessageBox(mainShell, SWT.ICON_ERROR | SWT.OK);
				mb.setText("系统提示");
				mb.setMessage("没有报文！");
				mb.open();
				return null;
			}
			return msgList;
		} catch (Exception ex) {
			Log.error(getClass().getName(), "readFile() error", ex);
			return null;
		}
	}

	private List<String> findMsgListFromFile(String file) {
		List<String> list = new ArrayList<>();
		InputStreamReader reader = null;// 考虑到编码格式
		try {
			reader = new InputStreamReader(new FileInputStream(file));
			BufferedReader bufferedReader = new BufferedReader(reader);
			String lineTxt = null;
			StringBuilder sb = new StringBuilder();
			Boolean flag = false;
			while ((lineTxt = bufferedReader.readLine()) != null) {
				//从文件头开始读
				if (lineTxt.contains("\001")) {
					flag = true;
				}
				//如果是文件尾部则是完整的一条报文，添加到list 中
				if (lineTxt.contains("\003")) {
					sb.append(lineTxt);
					flag = false;
					list.add(sb.toString());
					sb.delete(0, sb.length());
				}
				if (flag) {
					sb.append(lineTxt + '\n');
				}
			}
		} catch (Exception ex) {
			Log.error(getClass().getName(), "findMsgListFromFile() error", ex);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ex) {
				Log.error(getClass().getName(), "reader close failed", ex);
			} finally {
				reader = null;
			}
		}
		return list;
	}
}
