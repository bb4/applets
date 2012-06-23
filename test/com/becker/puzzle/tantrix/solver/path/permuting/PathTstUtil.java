// Copyright by Barry G. Becker, 2012. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.becker.puzzle.tantrix.solver.path.permuting;

import com.becker.common.geometry.Location;
import com.becker.puzzle.tantrix.model.*;
import com.becker.puzzle.tantrix.solver.path.TantrixPath;

import static com.becker.puzzle.tantrix.TantrixTstUtil.TILES;

/**
 * @author Barry Becker
 */
class PathTstUtil {

    static final Location LOWER_LEFT = new Location(22, 20);
    static final Location LOWER_RIGHT = new Location(22, 21);
    static final Location UPPER = new Location(21, 21);
    static final Location UPPER_LEFT = new Location(21, 20);

    static final HexTile TILE1 = TILES.getTile(1);
    static final HexTile TILE2 = TILES.getTile(2);
    static final HexTile TILE3 = TILES.getTile(3);
    static final HexTile TILE4 = TILES.getTile(4);

    static final TantrixPath LOOP_PATH =
            createPath(
                    new TilePlacement(TILE2, LOWER_RIGHT, Rotation.ANGLE_60),
                    new TilePlacement(TILE1, UPPER, Rotation.ANGLE_0),
                    new TilePlacement(TILE3, LOWER_LEFT, Rotation.ANGLE_120));

    static final TantrixPath NON_LOOP_PATH3 =
            createPath(
                    new TilePlacement(TILE2, LOWER_RIGHT, Rotation.ANGLE_0),
                    new TilePlacement(TILE1, UPPER, Rotation.ANGLE_0),
                    new TilePlacement(TILE3, LOWER_LEFT, Rotation.ANGLE_120));

    static final TantrixPath LOOP_PATH4 = new TantrixPath(
            new TilePlacementList(
                    new TilePlacement(TILE1, LOWER_LEFT, Rotation.ANGLE_0),
                    new TilePlacement(TILE3, LOWER_RIGHT, Rotation.ANGLE_0),
                    new TilePlacement(TILE4, UPPER, Rotation.ANGLE_60),
                    new TilePlacement(TILE2, UPPER_LEFT, Rotation.ANGLE_60)),
            PathColor.RED);

    static final TantrixPath NON_LOOP_PATH4 = new TantrixPath(
            new TilePlacementList(
                    new TilePlacement(TILE1, LOWER_LEFT, Rotation.ANGLE_120),
                    new TilePlacement(TILE2, UPPER_LEFT, Rotation.ANGLE_60),
                    new TilePlacement(TILE4, UPPER, Rotation.ANGLE_60),
                    new TilePlacement(TILE3, LOWER_RIGHT, Rotation.ANGLE_300)),
            PathColor.RED);



    static TantrixPath createPath(TilePlacement placement1, TilePlacement placement2, TilePlacement placement3) {
        return  new TantrixPath(new TilePlacementList(placement1, placement2, placement3), PathColor.YELLOW);
    }

}
