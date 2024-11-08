package org.ate.unisim.main.buildings;

public class LectureHall extends Building {
    /**
     * Creates a new instance of `LectureHall`.
     */
    public LectureHall() {
        super();

        type = Types.LECTURE_HALL;
        rows = 8;
        cols = 11;
        filename = "lecture-hall.png";
    }
}
