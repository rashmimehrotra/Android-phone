package com.app.learniatstudent.Constants;

/**
 * Created by macbookpro on 21/02/16.
 */
public class SLConstants {

    public static String kConstantLogin = "Connecting to Learniat server....";
    public static String kConstantAuthSuccessful1 = "Authentication successful with Learniat server.";
    public static String kConstantAuthSuccessful2 = " obtained. Connecting to Xmpp server.";
    public static String kConstantXMPPConnected = "Successfully authenticated with Learniat server.\nStream successfully opened with XMPP server.\n User authenticating in XMPP server....";

    // XMPP messages types
    public static final int kTeacherLogin = 101;
    public static final int kTeacherSRQmodeEnabled = 161;
    public static final int kSeatingChanged = 195;
    public static final int kQuestionName = 171;
    public static final int kModelAnswerDetails = 179;
    public static final int kTimeExtended = 180;
    public static final int kQueryAnswering = 182;
    public static final int kQueryCloseVoting = 185;
    public static final int kEndVolunteeringSession = 187;
    public static final int kSharegraph = 190;
    public static final int kTeacherQnASubmitted = 231;
    public static final int kTeacherQnAFreeze = 232;
    public static final int kTeacherQnADone = 233;
    public static final int kTeacherEndsSession = 706;
    public static final int kCollaborationPing = 713;
    public static final int kCloseCollaboration = 715;
    public static final int kCreatedRoomName = 722;
    public static final int kStudentSentBenchState = 220;
    public static final int kAllowVoting = 173;
    public static final int kQuestionLabel = 170;
    public static final int kQuestionRoom = 723;
    public static final int kTeacherHandRaiseInReview = 223;
    public static final int kShareGraph = 724;
    public static final int kSendFeedBack = 701;
    public static final int kStudentQnAAccept = 217;
    public static final int kStudentSendAnswer = 321;



    //Observer Constants
    public static String kXMPPConnectedSuccessfully = "kxmppconnectedsuccessfully";
    public static String kXMPPReConnectedSuccessfully = "kxmppreconnectedsuccessfully";
    public static String kXMPPMsgSeatChanged = "kxmppmsgseatchanged";
    public static String kXMPPMsgTimeExtended= "kxmppmsgtimeextended";
    public static String kXMPPMsgTeacherEndsSessions= "kxmppmsgteacherendsession";
    public static String kXMPPMsgAllowVoting= "kxmppmsgallowvoting";
    public static String kXMPPMsgTeacherQnASubmitted= "kxmppmsgteacherqnasubmitted";


    // Fonts
    public static String kFontRobotoBlack = "fonts/Roboto-Black.ttf";
    public static String kFontRobotoBlackItalic = "fonts/Roboto-BlackItalic.ttf";
    public static String kFontRobotoBold = "fonts/Roboto-Bold.ttf";
    public static String kFontRobotoBoldItalic = "fonts/Roboto-BoldItalic.ttf";
    public static String kFontRobotoItalic = "fonts/Roboto-Italic.ttf";
    public static String kFontRobotoLight = "fonts/Roboto-Light.ttf";
    public static String kFontRobotoLightItalic = "fonts/Roboto-LightItalic.ttf";
    public static String kFontRobotoRegular = "fonts/Roboto-Regular.ttf";

}
