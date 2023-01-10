package com.xiaoyv.blueprint.main;

import java.util.ArrayList;
import java.util.List;

/**
 * Table
 *
 * @author why
 * @since 2023/1/10
 */
public class Table {

    /**
     * 课程表
     */
    public static class CourseCurriculum {
        private static final long serialVersionUID = 9153036347240575395L;
        private List<Curriculum> curriculums = new ArrayList<>();

        public List<Curriculum> getCurriculums() {
            return curriculums;
        }

        public void setCurriculums(List<Curriculum> curriculums) {
            this.curriculums = curriculums;
        }
    }

    /**
     * 课程格子
     */
    public static class Curriculum {
        private static final long serialVersionUID = 6775594353970223854L;
        // 是否为空课程
        private boolean isEmpty = true;
        // mid：999（自定义课程）
        private String mid = "";
        // 课程名称
        private String name = "暂无";
        // 课程老师
        private String teacher = "";
        // 课程老师ID
        private String teacher_id = "";
        // 课程教室
        private String room = "";
        // 课程教室ID
        private String roomId = "";
        // 课程是星期几
        private String week = "";
        // 课程是第几大节
        private String section = "0";
        // 课程所有周次安排（0|1）
        private String allWeek = "";
        // 课程背景色
        private String bgColor = "FFFFFF";
        // 课程背景色透明度
        private String bgColorAlpha = "AA";
        // 冲突课程
        private List<Curriculum> crashCourses = new ArrayList<>(); // 不影响数据库存入，只有在Adapter内才赋值

        public boolean isEmpty() {
            return isEmpty;
        }

        public void setEmpty(boolean empty) {
            isEmpty = empty;
        }

        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            name = name.replace("\"", "");
            this.name = name;
        }

        public String getTeacher() {
            return teacher;
        }

        public void setTeacher(String teacher) {
            this.teacher = teacher;
        }

        public String getTeacherId() {
            return teacher_id;
        }

        public void setTeacherId(String teacher_id) {
            this.teacher_id = teacher_id;
        }

        public String getRoom() {
            return room;
        }

        public void setRoom(String room) {
            room = room.replace("\"", "");
            this.room = room;
        }

        public String getRoomId() {
            return roomId;
        }

        public void setRoomId(String roomId) {
            this.roomId = roomId;
        }

        public String getWeek() {
            return week;
        }

        public void setWeek(String week) {
            this.week = week;
        }

        public String getSection() {
            return section;
        }

        public void setSection(String section) {
            this.section = section;
        }

        public String getAllWeek() {
            return allWeek;
        }

        public void setAllWeek(String allWeek) {
            allWeek = allWeek.replace("\"", "");
            this.allWeek = allWeek;
        }

        public List<Curriculum> getCrashCourses() {
            return crashCourses;
        }

        public void setCrashCourses(List<Curriculum> crashCourses) {
            this.crashCourses = crashCourses;
        }

        public String getBgColor() {
            return bgColor;
        }

        public void setBgColor(String bgColor) {
            this.bgColor = bgColor;
        }

        public String getBgColorAlpha() {
            return bgColorAlpha;
        }

        public void setBgColorAlpha(String bgColorAlpha) {
            this.bgColorAlpha = bgColorAlpha;
        }

        public void fillCourse(Curriculum course) {
            this.crashCourses = course.getCrashCourses();
            this.isEmpty = course.isEmpty();
            this.bgColor = course.getBgColor();
            this.bgColorAlpha = course.getBgColorAlpha();
            this.mid = course.getMid();
            this.name = course.getName();
            this.teacher = course.getTeacher();
            this.teacher_id = course.getTeacherId();
            this.room = course.getRoom();
            this.roomId = course.getRoomId();
            this.week = course.getWeek();
            this.section = course.getSection();
            this.allWeek = course.getAllWeek();
        }
    }

}
