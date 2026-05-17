<template>
  <PracticeDisplay :course-section-id="courseSectionId" />
</template>

<script setup>
import { computed } from "vue";
import { useRoute } from "vue-router";
import PracticeDisplay from "@/components/practice/PracticeDisplay.vue";

const route = useRoute();

const courseSectionId = computed(() => {
  const parsed = Number(route.params.id);
  return Number.isFinite(parsed) && parsed > 0 ? parsed : null;
});
</script>
