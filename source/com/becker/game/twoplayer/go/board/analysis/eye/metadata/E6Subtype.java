package com.becker.game.twoplayer.go.board.analysis.eye.metadata;

import com.becker.common.Box;
import com.becker.game.twoplayer.go.board.GoBoardPosition;
import com.becker.game.twoplayer.go.board.GoEye;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeNeighborMap;
import com.becker.game.twoplayer.go.board.analysis.eye.EyeStatus;

import java.util.ArrayList;
import java.util.List;

import static com.becker.game.twoplayer.go.board.analysis.eye.EyeShapeScores.*;

/**
 * Subtype containing MetaData for the different possible Eye shapes of size 6.
 * There are 8 different subtypes to consider.
 *
 * @author Barry Becker
 */
public class E6Subtype extends AbstractEyeSubtype
{
    /** Different sorts of eye with 6 spaces. */
    enum Subtype {E112222, E111223, E111133, E112233, E112233a, E112233b, E122223, E112224, E111124, E222233}
    private Subtype type;

    /**
     * Constructor
     * @param subTypeDesc description of the type - something like "E112223".
     */
    E6Subtype(String subTypeDesc) {
        type = Subtype.valueOf(subTypeDesc);
        switch(type) {
           case E112222 : initialize(true, 6, 13, GUARANTEED_TWO_EYES);
               break;
           case E111223 : initialize(true, 6, 12, GUARANTEED_TWO_EYES);
               break;
           case E111133 : initialize(true, 6, 1, GUARANTEED_TWO_EYES);
               break;
           case E112233 : initialize(false, 6, 4, PROBABLE_TWO_EYES);
               break;
           case E112233a : initialize(false, 6, 2, PROBABLE_TWO_EYES, new float[] {3.06f, 3.06f});
               break;
           case E112233b : initialize(false, 6, 2, SINGLE_EYE, new float[] {3.05f, 3.05f, 2.06f, 2.06f});
               break;
           case E122223 : initialize(false, 6, 2, PROBABLE_TWO_EYES, new float[] {2.04f, 3.06f, 2.05f, 2.05f, 2.04f},
                                                                     new float[] {1.02f});
               break;
           case E112224 : initialize(false, 6, 1, PROBABLE_TWO_EYES, new float[] {4.06f},
                                                                     new float[] {2.04f});
               break;
           case E111124 : initialize(false, 6, 1, PROBABLE_TWO_EYES, new float[] {2.05f, 4.05f},
                                                                     new float[] {1.02f});
               break;
           case E222233 : initialize(false, 6, 1, PROBABLE_TWO_EYES, new float[] {3.07f, 3.07f});
               break;
        }
    }


    /**
     * @return eye status for E6 types.
     */
    @Override
    public EyeStatus determineStatus(GoEye eye, EyeNeighborMap nbrMap) {
        switch (type) {
            case E112222 :
            case E111223 :
            case E111133 :
                handleSubtypeWithLifeProperty();
            case E112233 :
                Subtype E112233Subtype = determineE112233Subtype(nbrMap);
                if (E112233Subtype == Subtype.E112233a) {
                   return handleVitalPointCases(nbrMap, eye, 2);
                }
                else {
                   return handleVitalPointCases(nbrMap, eye, 4);
                }
            case E122223 :
                return handleVitalPointCases(nbrMap, eye, 4);
            case E112224 :
                List<GoBoardPosition> endFilledSpaces = findSpecialFilledSpaces(nbrMap, getEndPoints(), eye);
                switch (endFilledSpaces.size())
                {
                    case 0 :  return handleVitalPointCases(nbrMap, eye, 2);
                    case 1 :  return handleVitalPointCases(nbrMap, eye, 1); // replace with handleLifeProp? see page 122
                    default : assert false : "unexpected number of end spaces filled";
                }
            case E111124 :
                return handleVitalPointCases(nbrMap, eye, 2);
            case E222233 :
                return handleVitalPointCases(nbrMap, eye, 2);

        }
        return EyeStatus.NAKADE; // never reached
    }

    /**
     * find the 2 spaces with only 1 nbr
     * if the box defined by those 2 positions contains the other 4 spaces, then case b, else a
     * @return the subtype E112233a or E112233b
     */
    private Subtype determineE112233Subtype(EyeNeighborMap nbrMap) {

        List<GoBoardPosition> oneNbrPoints = new ArrayList<GoBoardPosition>(2);
        List<GoBoardPosition> otherPoints = new ArrayList<GoBoardPosition>(4);

        for (GoBoardPosition pos : nbrMap.keySet()) {
            if (nbrMap.getNumEyeNeighbors(pos) == 1)  {
               oneNbrPoints.add(pos);
            }
            else {
                otherPoints.add(pos);
            }
        }
        assert oneNbrPoints.size() == 2;
        Box bounds = new Box(oneNbrPoints.get(0).getLocation(), oneNbrPoints.get(1).getLocation());

        for (GoBoardPosition otherPt : otherPoints) {
            if (!bounds.contains(otherPt.getLocation())) {
                return Subtype.E112233a;
            }
        }
        return Subtype.E112233b;
    }

    public String getTypeName() {
        return type.toString();
    }
}