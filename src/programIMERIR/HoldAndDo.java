package programIMERIR;


import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import com.kuka.common.ThreadUtil;
import com.kuka.roboticsAPI.applicationModel.RoboticsAPIApplication;
import static com.kuka.roboticsAPI.motionModel.BasicMotions.*;

import com.kuka.roboticsAPI.deviceModel.JointPosition;
import com.kuka.roboticsAPI.deviceModel.LBR;
import com.kuka.roboticsAPI.geometricModel.CartDOF;
import com.kuka.roboticsAPI.geometricModel.ObjectFrame;
import com.kuka.roboticsAPI.geometricModel.Tool;
import com.kuka.roboticsAPI.geometricModel.Workpiece;
import com.kuka.roboticsAPI.motionModel.IMotion;
import com.kuka.roboticsAPI.motionModel.controlModeModel.CartesianImpedanceControlMode;
import com.kuka.roboticsAPI.motionModel.controlModeModel.JointImpedanceControlMode;
import com.kuka.roboticsAPI.motionModel.controlModeModel.PositionControlMode;
import com.kuka.roboticsAPI.uiModel.ApplicationDialogType;

/**
 * Implementation of a robot application.
 * <p>
 * The application provides a {@link RoboticsAPITask#initialize()} and a 
 * {@link RoboticsAPITask#run()} method, which will be called successively in 
 * the application lifecycle. The application will terminate automatically after 
 * the {@link RoboticsAPITask#run()} method has finished or after stopping the 
 * task. The {@link RoboticsAPITask#dispose()} method will be called, even if an 
 * exception is thrown during initialization or run. 
 * <p>
 * <b>It is imperative to call <code>super.dispose()</code> when overriding the 
 * {@link RoboticsAPITask#dispose()} method.</b> 
 * 
 * @see UseRoboticsAPIContext
 * @see #initialize()
 * @see #run()
 * @see #dispose()
 */
public class HoldAndDo extends RoboticsAPIApplication {
	@Inject
	private LBR robot;

//	CartesianImpedanceControlMode mode;
	JointImpedanceControlMode mode;
	
	int onPosition = -1;
	double[] jointPosition;
	
	@Override
	public void initialize() {
		onPosition = -1;

//		mode = new CartesianImpedanceControlMode();
//		mode.parametrize(CartDOF.TRANSL).setStiffness(100);
//		mode.parametrize(CartDOF.ROT).setStiffness(10);

		mode = new JointImpedanceControlMode(100, 100, 100, 100, 100, 10, 100);
		mode.setStiffness(100, 100, 100, 10, 10, 10, 10);
	}

	@Override
	public void run() {
		//robot.move(ptpHome());
		
		onPosition = getApplicationUI().displayModalDialog(ApplicationDialogType.QUESTION, "Voulez-vous positionner le robot?", "Oui", "Non");
		while(onPosition != 0){
			robot.move(positionHold(mode, 5, TimeUnit.SECONDS));
			
			jointPosition = robot.getCurrentJointPosition().get();
			
			robot.move(ptp(jointPosition));
			onPosition = getApplicationUI().displayModalDialog(ApplicationDialogType.QUESTION, "Voulez-vous positionner le robot?", "Oui", "Non");
		}
	}
}