import { defineStore } from "pinia";
import { ref } from "vue";

export const teacherStore = defineStore('teacher', () => {
    //State
    const chosenCourseSectionId = ref(0);
    const chosenCourseId = ref(0);
    const chosenPracticeId = ref(0);
    //Action
    const chooseCourseSection = (id) => {
        chosenCourseSectionId.value = id
    }
    const chooseCourse = (id) => {
        chosenCourseId.value = id
    }
    const choosePractice = (id) => {
        chosenPracticeId.value = id
    }
    return {
        //State
        chosenCourseSectionId,
        chosenCourseId,
        chosenPracticeId,
        //Action
        chooseCourseSection,
        chooseCourse,
        choosePractice,
    }
})