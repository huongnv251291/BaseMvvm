//package com.example.basemvvm.interceptors.demo
//
//import android.util.Log
//import com.hnv.BaseRepository
//
//class DeliveryRepository(
//    private val kvApi: KvApi,
//    private val partnerDeliveryDb: PartnerDeliveryDb,
//    private val partnerDeliveryGroupDetailDb: PartnerDeliveryGroupDetailDb,
//    private val cacheObjectDb: CacheObjectDb
//) : BaseRepository {
//    suspend fun fetchPartnersDelivery(forceRenew: Boolean) {
//        val lastSync = cacheObjectDb.getSyncTimeStamp(CacheObjectDb.KV_DELIVERY_PARTNER_TIMESTAMP)
//        var res: KvRevisionResponse<ApiPartnerDelivery>? = null
//        if (lastSync == 0L) {
//            res = kvApi.syncPartnerDeliveries()
//        } else if (forceRenew || System.currentTimeMillis() - lastSync >= Constants.SYNC_DELAY) {
//            val lastModifiedDate = cacheObjectDb.getRevisionTimeStamp(CacheObjectDb.KV_DELIVERY_PARTNER_REVISION_TIMESTAMP)
//            res = kvApi.syncPartnerDeliveries(lastModifiedDate)
//        }
//        res?.let {
//            partnerDeliveryDb.saveOrUpdate(it.data.map { it.convert() })
//            it.removedIds?.let {
//                partnerDeliveryDb.deleteById(it)
//            }
//            cacheObjectDb.saveRevisionTimeStamp(CacheObjectDb.KV_DELIVERY_PARTNER_REVISION_TIMESTAMP, it.timeStamp)
//            cacheObjectDb.saveSyncTimeStamp(CacheObjectDb.KV_DELIVERY_PARTNER_TIMESTAMP, System.currentTimeMillis())
//            Log.d(Constants.KV_LOG + "Sync", "Sync Partner Deliveries done with ${res.data.size} items")
//        }
//
//    }
//
//    fun getPartnersDelivery(query: String): List<PartnerDelivery> {
//        return partnerDeliveryDb.getAvailablePartnersDelivery(query)
//    }
//
//    fun getPartnerDeliveryGroupDetails(partnerDeliveryId: Long): List<PartnerDeliveryGroupDetail> {
//        return partnerDeliveryGroupDetailDb.findByPartnerDeliveryId(partnerDeliveryId)
//    }
//
//    suspend fun getLastSyncTimeStamp(): Long {
//        return cacheObjectDb.getSyncTimeStamp(CacheObjectDb.KV_DELIVERY_PARTNER_TIMESTAMP)
//    }
//}