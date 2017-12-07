/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.AnalogSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;



@TeleOp(name="TeleOp", group="Linear Opmode")

public class MIM_TeleOp extends LinearOpMode
{
    // this sensor looks at the jewel and reports the color as 3 RGB integer values
    private ColorSensor jewelColor;

    // this it the sensor that measures the height of the main arm.
    private AnalogInput armPosition;



    private DcMotor frontLeftDrive;
    private DcMotor frontRightDrive;
    private DcMotor rearLeftDrive;
    private DcMotor rearRightDrive;
    private DcMotor armDrive;

    private Servo jewelServo;
    private Servo leftGrabA;
    private Servo rightGrabA;
    private Servo leftGrabB;
    private Servo rightGrabB;
    private Servo flipServo;
    private Servo tempServo1;
    private Servo tempServo2;



    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();


    @Override
    public void runOpMode()
    {
        telemetry.addData("Status", "Initialized");
        telemetry.update();
        
        // Setup a variable for each drive wheel to save power level for telemetry
        double leftPower = 0;
        double rightPower = 0;

        double leftStick1 = 0;
        double rightStick1 = 0;
        
        double grabAButtonTime = 0;

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        frontLeftDrive  = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRightDrive = hardwareMap.get(DcMotor.class, "frontRight");
        rearLeftDrive = hardwareMap.get (DcMotor.class, "rearLeft");
        rearRightDrive = hardwareMap.get (DcMotor.class, "rearRight");
        armDrive = hardwareMap.get (DcMotor.class, "armDrive");

        jewelServo = hardwareMap.servo.get("jewelServo");
        leftGrabA = hardwareMap.servo.get("leftGrabA");
        rightGrabA = hardwareMap.servo.get("rightGrabA");
        leftGrabB = hardwareMap.servo.get("leftGrabB");
        rightGrabB = hardwareMap.servo.get("rightGrabB");
        flipServo = hardwareMap.servo.get("flipServo");

        jewelColor = hardwareMap.colorSensor.get("jewelColor");
        jewelColor.enableLed(true);
        
        armPosition = hardwareMap.analogInput.get("armPos");


        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        rearLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        rearRightDrive.setDirection(DcMotor.Direction.REVERSE);
        armDrive.setDirection(DcMotor.Direction.REVERSE);

        // Start position of the jewel bumping arm (up)
        jewelServo.setPosition(1);

        leftGrabA.setPosition(0);
        rightGrabA.setPosition(1);
        leftGrabB.setPosition(0);
        rightGrabB.setPosition(1);
        flipServo.setPosition(0);

        armDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        boolean flipTimerFlag = false;
        boolean flipButtonFlag = false;
        boolean aGrabFlag = false;
        boolean aGrabTimerFlag = false;
        boolean bGrabFlag = false;
        boolean bGrabTimerFlag = false;
        boolean flipLock = false;
        boolean aGrabLock = false;
        boolean bGrabLock = false;
        boolean aUpFlag = false; //True when aGrab is on top


        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive())
        {



            leftStick1 =  gamepad1.left_stick_y;
            rightStick1 = gamepad1.right_stick_y;

            double leftRamp = Math.pow(Math.abs(leftStick1), 2.5);
            double rightRamp = Math.pow(Math.abs(rightStick1), 2.5);

          if( gamepad1.left_stick_y < 0 )
          {
            leftRamp = -leftRamp;
          }

          if( gamepad1.right_stick_y < 0 )
          {
            rightRamp = -rightRamp;
          }

        //When the dpad is pressed up or down and the flag is false then the arm moves up or down as arm runtime is tracked,
            // otherwise it is stationary and and arm runtime is set to zero and arm off time begins tracking
          if(gamepad2.dpad_up)
          {
            armDrive.setPower(0.2);
          }
          else if (gamepad2.dpad_down)
              {
            armDrive.setPower(-0.1);
          }
          else
          {
              armDrive.setPower(0);
          }

          if(gamepad2.y && flipServo.getPosition() < 1 )
            {
                flipServo.setPosition(1);
            }

            else if(gamepad2.y && flipServo.getPosition() == 1)
            {
                flipServo.setPosition(0);
            }





            //Clips the left or right drive powers to 1 if it is > 1 and to -1 if it is < -1
            // (sets the values to between 1 and -1)
            leftPower = Range.clip( leftRamp, -1.0, 1.0 );
            rightPower = Range.clip( rightRamp, -1.0, 1.0 );


            // Send calculated power to wheels
            frontLeftDrive.setPower(leftPower);
            rearLeftDrive.setPower(leftPower);

            frontRightDrive.setPower(rightPower);
            rearRightDrive.setPower(rightPower);
            


            // Show the elapsed game time and wheel power.
            telemetry.addData("Servo LeftA Position", leftGrabA.getPosition());
            telemetry.addData("Servo RightA Position", rightGrabA.getPosition());
            telemetry.addData("Servo LeftB Position", leftGrabB.getPosition());
            telemetry.addData("Servo RightB Position", rightGrabB.getPosition());
            telemetry.addData("Servo Flip Position", flipServo.getPosition());
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Motors", "left (%.2f), right (%.2f)", leftPower, rightPower);
            telemetry.update();
        }
    }

    public void grabSwap()
    {
        // swap the servos assigned to A & B
        tempServo1 = leftGrabA;
        tempServo2 = rightGrabA;
        leftGrabA  = leftGrabB;
        rightGrabA = rightGrabB;
        leftGrabB  = tempServo1;
        rightGrabB = tempServo2;
    }
}

