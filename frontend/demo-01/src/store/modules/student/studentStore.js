import { defineStore } from "pinia";
import { ref } from "vue";

export const studentStore = defineStore('student', () => {
    //State
    const chosenCourseSectionId = ref(0);
    const chosenCourseId = ref(0);
    //Action
    const chooseCourseSection = (id) => {
        chosenCourseSectionId.value = id
    }
    const chooseCourse = (id) => {
        chosenCourseId.value = id
    }
    return {
        //State
        chosenCourseSectionId,
        chosenCourseId,
        //Action
        chooseCourseSection,
        chooseCourse,
    }
})