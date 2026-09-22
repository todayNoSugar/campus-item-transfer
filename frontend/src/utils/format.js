/**
 * 通用时间格式化：yyyy-MM-dd HH:mm
 */
export function formatTime(value) {
  if (!value) return ''
  const d = new Date(value)
  if (isNaN(d.getTime())) return ''
  const pad = (n) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}`
}

/** 物品状态文案 */
export const GOODS_STATUS = {
  0: { text: '待审核', type: 'warning' },
  1: { text: '上架中', type: 'success' },
  2: { text: '已成交', type: 'info' },
  3: { text: '已下架', type: 'danger' }
}

export function goodsStatusText(status) {
  return GOODS_STATUS[status]?.text || '未知'
}

/** 预约状态文案 */
export const RESERVE_STATUS = {
  0: { text: '待确认', type: 'warning' },
  1: { text: '已同意', type: 'success' },
  2: { text: '已拒绝', type: 'danger' }
}

export function reserveStatusText(status) {
  return RESERVE_STATUS[status]?.text || '未知'
}
