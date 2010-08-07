package com.becker.game.twoplayer.go.board.analysis.eye.information;

import com.becker.game.twoplayer.go.board.GoBoard;
import com.becker.game.twoplayer.go.board.elements.GoEye;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeShapeScores;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeStatus;

/**
 * Info about a false eye
 *
 * @author Barry Becker
 */
public class FalseEyeInformation extends AbstractEyeInformation {
    
    public EyeStatus determineStatus(GoEye eye, GoBoard board) {
        if (eye.getMembers().size() > 5)  {
            return EyeStatus.NAKADE;
        }
        if (eye.getMembers().size() > 2)  {
            return EyeStatus.UNSETTLED;
        }
        else return EyeStatus.KO;
    }

    public String getTypeName() {
        return "False";
    }
}
