package org.ate.unisim.main.buildings;

public class LectureHall extends Building {
    /**
     * Creates a new instance of `LectureHall`.
     */
    public LectureHall() {
        super();

        type = Types.LECTURE_HALL;
        rows = 9;
        cols = 12;
        filename = "lecture-hall.png";
    }
}
