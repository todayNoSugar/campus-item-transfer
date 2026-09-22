<template>
  <el-avatar :size="size" :src="src || ''">
    {{ fallbackText }}
  </el-avatar>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  src: { type: String, default: '' },
  name: { type: String, default: '?' },
  size: { type: Number, default: 40 }
})

// 无头像或加载失败时显示昵称首字
const fallbackText = computed(() => (props.name || '?').slice(0, 1))
</script>
