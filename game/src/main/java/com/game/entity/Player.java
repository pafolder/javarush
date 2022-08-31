package com.game.entity;

import javax.persistence.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Entity
@Table(name = "player")
public class Player {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;    // BIGINT(20) AUTO_INCREMENT
    @Column
    private String name;        // VARCHAR(12) NULL
    @Column
    private String title;       // VARCHAR(30) NULL
    @Column
    @Enumerated(EnumType.STRING)
    private Race race;              // VARCHAR(20)
    @Column
    @Enumerated(EnumType.STRING)
    private Profession profession;  // VARCHAR(20)
    @Column
    private Date birthday;          // DATE
    @Column
    private Boolean banned;         // BIT(1)
    @Column
    private Integer experience;     // INT(10)
    @Column
    private Integer level;          // INT(3)
    @Column
    private Integer untilNextLevel; // INT(10)

    public boolean isValid() {
        int year = 0;
        if (birthday != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(birthday);
            year = calendar.get(Calendar.YEAR);
        }

        return
                name != null && name.length() != 0 && name.length() <= 12 &&
                title != null && title.length() <= 30 &&
                birthday != null && year >= 2000 && year <= 3000 &&
                profession != null && race != null &&
                getExperience() >= 0 && getExperience() <= 10000000L;
    }

    public void computeLevelAndUntilNextLevel()
    {
        level = (int) ((Math.sqrt(2500.0 + 200 * experience) - 50) / 100) ;
        untilNextLevel = 50 * (level + 1) * (level + 2) - experience;
    }

    public boolean isEmpty() { // id is not accounted
        return name == null && title == null && birthday == null &&
                experience == null && level == null && untilNextLevel == null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public Date getBirthday() {
        return birthday;
    }

    public String getBirthdayString() {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        return sd.format(getBirthday());
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Boolean isBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public int getUntilNextLevel() {
        return untilNextLevel;
    }

    public void setUntilNextLevel(int untilNextLevel) {
        this.untilNextLevel = untilNextLevel;
    }

    public boolean areNonNullValid() {
        int year = 0;
        if (birthday != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(birthday);
            year = calendar.get(Calendar.YEAR);
        }

        return (name == null || (name.length() != 0 && name.length() <= 12)) &&
                (title == null || (title.length() <= 30)) &&
                (birthday == null || (year >= 2000 && year <= 3000)) &&
                (experience == null || (experience >= 0 && experience <= 10000000L));
    }
}
