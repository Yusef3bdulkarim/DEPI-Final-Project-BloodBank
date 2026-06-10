package com.example.depi_final_project_bloodbank.utils

import com.google.firebase.firestore.FirebaseFirestore

// TEMPORARY FILE — delete after seeding test data
fun seedBloodRequests(
    onSuccess: () -> Unit = {},
    onFailure: (Exception) -> Unit = {}
) {
    val db = FirebaseFirestore.getInstance()
    val collection = db.collection("Requests")
    val now = System.currentTimeMillis()
    val day = 24 * 60 * 60 * 1000L

    val requests = listOf(
        mapOf(
            "bloodType" to "O-",
            "hospitalName" to "Kafr El-Sheikh General Hospital",
            "governorate" to "Kafr El-Sheikh",
            "city" to "Kafr El-Sheikh",
            "hospitalLat" to 31.1107,
            "hospitalLng" to 30.9388,
            "contactName" to "Dr. Mohamed Ali",
            "contactPhone" to "01012345678",
            "unitsNeeded" to 3,
            "unitsReserved" to 1,
            "unitsConfirmed" to 0,
            "status" to "ACTIVE",
            "priority" to "URGENT",
            "createdBy" to "seed",
            "createdAt" to (now - 2 * day),
            "expiresAt" to (now + 5 * day)
        ),
        mapOf(
            "bloodType" to "A+",
            "hospitalName" to "University Hospital",
            "governorate" to "Kafr El-Sheikh",
            "city" to "Kafr El-Sheikh",
            "hospitalLat" to 31.1150,
            "hospitalLng" to 30.9420,
            "contactName" to "Dr. Sara Hassan",
            "contactPhone" to "01098765432",
            "unitsNeeded" to 2,
            "unitsReserved" to 0,
            "unitsConfirmed" to 0,
            "status" to "ACTIVE",
            "priority" to "NORMAL",
            "createdBy" to "seed",
            "createdAt" to (now - 1 * day),
            "expiresAt" to (now + 7 * day)
        ),
        mapOf(
            "bloodType" to "B+",
            "hospitalName" to "El-Sayed Galal Hospital",
            "governorate" to "Cairo",
            "city" to "Nasr City",
            "hospitalLat" to 30.0626,
            "hospitalLng" to 31.3219,
            "contactName" to "Dr. Ahmed Khaled",
            "contactPhone" to "01155667788",
            "unitsNeeded" to 4,
            "unitsReserved" to 2,
            "unitsConfirmed" to 1,
            "status" to "ACTIVE",
            "priority" to "URGENT",
            "createdBy" to "seed",
            "createdAt" to (now - 3 * day),
            "expiresAt" to (now + 4 * day)
        ),
        mapOf(
            "bloodType" to "AB-",
            "hospitalName" to "Mansoura University Hospital",
            "governorate" to "Dakahlia",
            "city" to "Mansoura",
            "hospitalLat" to 31.0409,
            "hospitalLng" to 31.3785,
            "contactName" to "Dr. Nour El-Din",
            "contactPhone" to "01234567890",
            "unitsNeeded" to 1,
            "unitsReserved" to 0,
            "unitsConfirmed" to 0,
            "status" to "ACTIVE",
            "priority" to "URGENT",
            "createdBy" to "seed",
            "createdAt" to now,
            "expiresAt" to (now + 3 * day)
        ),
        mapOf(
            "bloodType" to "O+",
            "hospitalName" to "Alexandria Main Hospital",
            "governorate" to "Alexandria",
            "city" to "Alexandria",
            "hospitalLat" to 31.2001,
            "hospitalLng" to 29.9187,
            "contactName" to "Dr. Fatma Ibrahim",
            "contactPhone" to "01122334455",
            "unitsNeeded" to 5,
            "unitsReserved" to 3,
            "unitsConfirmed" to 2,
            "status" to "ACTIVE",
            "priority" to "NORMAL",
            "createdBy" to "seed",
            "createdAt" to (now - 4 * day),
            "expiresAt" to (now + 6 * day)
        ),
        mapOf(
            "bloodType" to "A-",
            "hospitalName" to "Tanta University Hospital",
            "governorate" to "Gharbia",
            "city" to "Tanta",
            "hospitalLat" to 30.7865,
            "hospitalLng" to 31.0004,
            "contactName" to "Dr. Youssef Gamal",
            "contactPhone" to "01099887766",
            "unitsNeeded" to 2,
            "unitsReserved" to 1,
            "unitsConfirmed" to 0,
            "status" to "ACTIVE",
            "priority" to "NORMAL",
            "createdBy" to "seed",
            "createdAt" to (now - 1 * day),
            "expiresAt" to (now + 8 * day)
        )
    )

    var completed = 0
    var failed = false

    requests.forEach { data ->
        collection.add(data)
            .addOnSuccessListener {
                completed++
                if (completed == requests.size) onSuccess()
            }
            .addOnFailureListener { e ->
                if (!failed) {
                    failed = true
                    onFailure(e)
                }
            }
    }
}