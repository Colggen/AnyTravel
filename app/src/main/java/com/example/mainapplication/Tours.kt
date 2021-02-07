    package com.example.mainapplication

    import java.io.Serializable


    class Tours : Serializable{

        var id: String? = null
        var tourName: String?=null
        var description: String ?= null
        var imageId: String?=null
        var dateAndTime: String?=null
        var companyName: String?= null
        var price: Long?=null
        var regPeople: Int? = null
        var phone: String?=null
        var numbersOfPeople: Int?=null
        var bookedId: String? = null

    }