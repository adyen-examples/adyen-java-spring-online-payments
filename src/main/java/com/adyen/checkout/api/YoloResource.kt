package com.adyen.checkout.api

import com.adyen.Client
import com.adyen.enums.Environment
import com.adyen.model.checkout.*
import com.adyen.model.payout.PayoutRequest
import com.adyen.model.payout.PayoutResponse
import com.adyen.service.Checkout
import com.adyen.service.Payout
import com.adyen.service.legalentitymanagement.LegalEntities
import java.util.*

data class YoloAmount(
    val currency: String,
    val value: Long
)

data class YoloAddress(
    val city: String,
    val country: String,
    val houseNumberOrName: String,
    val postalCode: String,
    val street: String
)

class DatabaseService{
    fun getCustomerAddress(customerId: String) : YoloAddress {
        return YoloAddress("Amsterdam", "NL", "1", "1234AB", "Street")
    }

    fun getDefaultAmount(): YoloAmount {
        return YoloAmount("EUR", 10000L)
    }
}


class AdyenConverter {
    fun convertAddress(address: YoloAddress): com.adyen.model.checkout.Address {
        return com.adyen.model.checkout.Address().city(address.city).country(address.country).houseNumberOrName(address.houseNumberOrName).postalCode(address.postalCode).street(address.street)
    }

    fun convertAmount(amount: YoloAmount): Amount {
        return Amount().currency(amount.currency).value(amount.value)
    }
}

class YoloConverter {
    fun convertAddress(address: com.adyen.model.checkout.Address): YoloAddress {
        return YoloAddress(address.city, address.country, address.houseNumberOrName, address.postalCode, address.street)
    }

    fun convertAmount(amount: Amount): YoloAmount {
        return YoloAmount(amount.currency, amount.value)
    }

}

class YoloResource {

    val merchantAccount = ""
    private val apiKey = ""
    private var client = Client(apiKey, Environment.TEST)

    val checkout = Checkout(client)
    val payout = Payout(client)
    val lem = LegalEntities(client)


    // Customer data
    private val billingAddress = DatabaseService().getCustomerAddress("123")
    private val defaultAmount = DatabaseService().getDefaultAmount()

    //Calling the APIs with customer data
    val sessionResult = sessions(defaultAmount, billingAddress)
    val payoutResult = payout(defaultAmount, billingAddress)
    val legalEntityResult = getLEgalEntity("123")


    fun sessions(zeAmount: YoloAmount, address: YoloAddress): CreateCheckoutSessionResponse {
        val orderRef = UUID.randomUUID().toString()
//        val amount = AdyenConverter().convertAmount(defaultAmount)
        val amount = com.adyen.model.payout.Amount().currency("EUR").value(10000L)
        val billingAddress = AdyenConverter().convertAddress(billingAddress)

        val checkoutSession = CreateCheckoutSessionRequest()
        checkoutSession.countryCode("NL")
        checkoutSession.merchantAccount(merchantAccount)
        checkoutSession.channel = CreateCheckoutSessionRequest.ChannelEnum.WEB
        checkoutSession.reference = orderRef // required

        checkoutSession.billingAddress = AdyenConverter().convertAddress(address)

        checkoutSession.amount = AdyenConverter().convertAmount(zeAmount)
        checkoutSession.lineItems = listOf(
            LineItem().quantity(1L).amountIncludingTax(5000L).description("Sunglasses"),
            LineItem().quantity(1L).amountIncludingTax(5000L).description("Headphones")
        )

        return checkout.sessions(checkoutSession)
    }


    fun getLEgalEntity(entityId: String): YoloAddress {
        val result = lem.retrieve(entityId)
        return YoloConverter().convertAddress(result.individual.residentialAddress)
    }

    fun payout(amount: YoloAmount, address: YoloAddress): PayoutResponse {

        val payoutRequest = PayoutRequest()
        payoutRequest.merchantAccount = merchantAccount
        payoutRequest.reference = UUID.randomUUID().toString()
        payoutRequest.amount = AdyenConverter().convertAmount(defaultAmount)
        payoutRequest.billingAddress = AdyenConverter().convertAddress(billingAddress)

        return payout.payout(payoutRequest)
    }

}
