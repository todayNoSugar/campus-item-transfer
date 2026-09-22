import request from './request'

// ===== 认证 =====
export const register = (data) => request.post('/auth/register', data)
export const login = (data) => request.post('/auth/login', data)
export const getInfo = () => request.get('/auth/info')

// ===== 个人中心 =====
export const updateProfile = (data) => request.put('/user/profile', data)
export const uploadFile = (formData) =>
  request.post('/user/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })

// ===== 分类 =====
export const getCategories = () => request.get('/category/list')

// ===== 物品 =====
export const getGoodsList = (params) => request.get('/goods', { params })
export const getGoodsDetail = (id) => request.get(`/goods/${id}`)
export const createGoods = (data) => request.post('/goods', data)
export const updateGoods = (id, data) => request.put(`/goods/${id}`, data)
export const updateGoodsStatus = (id, status) => request.put(`/goods/${id}/status`, { status })
export const deleteGoods = (id) => request.delete(`/goods/${id}`)
export const getMyGoods = (params) => request.get('/goods/my', { params })

// ===== 留言 =====
export const getMessages = (goodsId) => request.get(`/goods/${goodsId}/messages`)
export const addMessage = (goodsId, content) => request.post(`/goods/${goodsId}/message`, { content })
export const replyMessage = (id, reply) => request.put(`/message/${id}/reply`, { reply })
export const deleteMessage = (id) => request.delete(`/message/${id}`)

// ===== 预约 =====
export const createReserve = (goodsId, data) => request.post(`/goods/${goodsId}/reserve`, data)
export const getMyReserves = () => request.get('/reserve/my')
export const getToMeReserves = () => request.get('/reserve/to-me')
export const updateReserveStatus = (id, status) => request.put(`/reserve/${id}/status`, { status })

// ===== 收藏 =====
export const addFavorite = (goodsId) => request.post(`/goods/${goodsId}/favorite`)
export const removeFavorite = (goodsId) => request.delete(`/goods/${goodsId}/favorite`)
export const getFavorites = () => request.get('/favorite/list')

// ===== 管理员 =====
export const adminGetUsers = (params) => request.get('/admin/user/list', { params })
export const adminUpdateUserStatus = (id, status) => request.put(`/admin/user/${id}/status`, { status })
export const adminGetGoods = (params) => request.get('/admin/goods/list', { params })
export const adminAuditGoods = (id, status) => request.put(`/admin/goods/${id}/audit`, { status })
export const adminOfflineGoods = (id) => request.put(`/admin/goods/${id}/offline`, { status: 3 })
export const adminGetMessages = (params) => request.get('/admin/message/list', { params })
export const adminDeleteMessage = (id) => request.delete(`/admin/message/${id}`)
export const adminGetStats = () => request.get('/admin/stats')
