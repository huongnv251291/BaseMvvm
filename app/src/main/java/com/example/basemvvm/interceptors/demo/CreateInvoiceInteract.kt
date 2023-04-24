//package com.example.basemvvm.interceptors.demo
//
//import kotlinx.coroutines.coroutineScope
//import kotlinx.coroutines.launch
//import net.citigo.kiotviet.pos.fb.data.api.models.ApiInvoice
//import net.citigo.kiotviet.pos.fb.data.api.models.Customer
//import net.citigo.kiotviet.pos.fb.data.api.models.PriceBook
//import net.citigo.kiotviet.pos.fb.data.repositories.DeliveryRepository
//import net.citigo.kiotviet.pos.fb.data.repositories.InvoiceRepository
//import net.citigo.kiotviet.pos.fb.data.repositories.ProductRepository
//import net.citigo.kiotviet.pos.fb.entities.enums.ChangeEnum
//import net.citigo.kiotviet.pos.fb.entities.enums.PaymentMethodEnum
//import net.citigo.kiotviet.pos.fb.entities.enums.PriceType
//import net.citigo.kiotviet.pos.fb.entities.kv.*
//import net.citigo.kiotviet.pos.fb.entities.kv.orderinvoice.fb.order.FBOrderDetail
//import net.citigo.kiotviet.pos.fb.interacts.Interact
//import net.citigo.kiotviet.pos.fb.ui.sale.Cart
//import net.citigo.kiotviet.pos.fb.utils.helpers.Utils
//
//class CreateInvoiceInteract(
//    private val invoiceRepository: InvoiceRepository,
//    private val productRepository: ProductRepository,
//    private val deliveryRepository: DeliveryRepository
//) : Interact<CreateInvoiceInteract.Param, Invoice>() {
//    data class Param(
//        val cart: Cart,
//        val customerPayment: Double,
//        val payments: List<Payment>,
//        val paymentOption: ChangeEnum,
//        val priceBook: PriceBook? = null,
//        val customer: Customer? = null,
//        val deliveryDetail: DeliveryDetail? = null,
//        val isQuickInvoice: Boolean = false,
//        val orderDetail: FBOrderDetail? = null
//    ) : Interact.Param()
//
//    override suspend fun execute(param: Param): Invoice {
//        val cart = param.cart
//        val invoice = Invoice()
//        if (param.customer != null && param.customer.id > 0)
//            invoice.customerId = param.customer.id
//
//        val invoiceDetails = mutableListOf<InvoiceDetail>()
//        cart.items.forEachIndexed { index, cartItem ->
//            val invoiceDetail = InvoiceDetail()
//            invoiceDetail.productId = cartItem.productId
//            invoiceDetail.productName = cartItem.name
//            invoiceDetail.productCode = cartItem.productCode
//            invoiceDetail.basePrice = cartItem.basePrice
//            invoiceDetail.price = cartItem.basePrice
//            invoiceDetail.discount = cartItem.discount
//            invoiceDetail.attributes = cartItem.attributes
//            invoiceDetail.productImage = cartItem.image
//            if (cartItem.discountType == PriceType.PERCENTAGE && cartItem.discount >= 0) {
//                invoiceDetail.discountRatio = (cartItem.discount / cartItem.basePrice) * 100
//            }
//            invoiceDetail.uuid = Utils.generateUUID()
//            invoiceDetail.quantity = cartItem.quantity
//            invoiceDetail.note = cartItem.note
//            invoiceDetail.unit = cartItem.unit
//            param.orderDetail?.let { orderDetail ->
//                orderDetail.orderDetails.find {
//                    it.productId == cartItem.productId
//                }?.let { detailDetail ->
//                    invoiceDetail.orderDetailId = detailDetail.id
//                }
//            }
//            invoiceDetails.add(invoiceDetail)
//        }
//        invoice.invoiceDetails = invoiceDetails
//        val invoiceOrderSurcharges = mutableListOf<InvoiceOrderSurcharge>()
//        var totalSurcharge = 0.0
//        cart.surcharges.filter { it.checked }.forEach {
//            invoiceOrderSurcharges.add(it.convertToInvoiceOrderSurcharge())
//            totalSurcharge += it.appliedValue
//        }
//        invoice.invoiceOrderSurcharges = invoiceOrderSurcharges
//        invoice.surcharge = totalSurcharge
//        invoice.totalBeforeDiscount = cart.subTotal
//        param.priceBook?.let {
//            if (it.id != -1L)
//                invoice.priceBookId = it.id
//        }
//        if (cart.discountType == PriceType.PERCENTAGE) {
//            invoice.discountRatio = cart.discount / cart.subTotal * 100
//        }
//        invoice.discount = cart.discount
//
//        if (param.payments.size == 1 && param.payments.sumOf { it.amount } != param.customerPayment) {
//            param.payments[0].amount = param.customerPayment
//        }
//        var customerPaid = param.payments.sumOf { it.amount }
//        if (cart.depositReturn > 0.0) {
//            invoice.depositReturn = cart.depositReturn
//            param.payments[0].amount = -cart.depositReturn
//            invoice.payments = param.payments
//            customerPaid = param.payments.sumOf { it.amount }
//        } else {
//            if (param.paymentOption == ChangeEnum.CHANGE || invoice.customerId == null) {
//                val processingPayment = mutableListOf<Payment>()
//                var remainder = cart.total
//                val priorityMethods =
//                    listOf(
//                        PaymentMethodEnum.CASH,
//                        PaymentMethodEnum.CARD,
//                        PaymentMethodEnum.TRANSFER
//                    )
//                var index = 0
//                while (remainder > 0 && index < priorityMethods.size) {
//                    param.payments.firstOrNull {
//                        it.method == priorityMethods[index].value
//                    }?.let {
//                        remainder -= it.amount
//                        if (remainder < 0) {
//                            it.amount += remainder
//                        }
//                        processingPayment.add(it)
//                    }
//                    index++
//                }
//                invoice.payingAmount = processingPayment.sumOf { it.amount }
//                invoice.payments = processingPayment
//            } else {
//                invoice.payingAmount = param.payments.sumOf { it.amount }
//                invoice.payments = param.payments
//            }
//        }
//        param.orderDetail?.let { orderDetail ->
//            invoice.orderCode = orderDetail.orderCode
//            invoice.orderId = orderDetail.id
//        }
//        invoice.total = cart.total
//        invoice.uuid = Utils.generateUUID()
//        param.deliveryDetail?.let {
//            if (param.deliveryDetail.enableInCart) {
//                if (it.partnerDelivery != null) {
//                    it.partnerDelivery?.id?.run {
//                        it.deliveryBy = this
//                        val pdgd = deliveryRepository.getPartnerDeliveryGroupDetails(this)
//                        if (pdgd.isNotEmpty()) {
//                            it.partnerDelivery?.partnerDeliveryGroupDetails = pdgd
//                        }
//                    }
//                }
//                if (param.deliveryDetail.useDefaultPartner) {
//                    param.deliveryDetail.expectedDelivery = null
//                }
//                invoice.isDeliveryEnable = true
//                invoice.deliveryDetail = param.deliveryDetail
//            }
//        }
//        invoice.description = cart.note
//        val payloadInvoice = ApiInvoice().convert(invoice)
//        if (param.isQuickInvoice) {
//            payloadInvoice.fromFbPos = false
//        }
//
//        val res = invoiceRepository.createInvoice(payloadInvoice, param.isQuickInvoice).convert()
//        if (res.id > -1) {
//            coroutineScope {
//                launch { productRepository.updateProductQuantityLocally(cart.items) }
//            }
//        }
//        res.invoiceDetails.forEach { invoiceDetail ->
//            invoiceDetails.find { it.uuid == invoiceDetail.uuid }?.let {
//                invoiceDetail.productCode = it.productCode
//                invoiceDetail.productName = it.productName
//                invoiceDetail.attributes = it.attributes
//                invoiceDetail.productImage = it.productImage
//                invoiceDetail.unit = it.unit
//            }
//        }
//        res.customerPaid = customerPaid
//        res.inDebt = param.paymentOption == ChangeEnum.CHANGE
//        res.invoiceOrderSurcharges = invoiceOrderSurcharges
//        res.deliveryDetail?.partnerDelivery=invoice.deliveryDetail?.partnerDelivery
//        return res
//    }
//}