package com.mobop.commutescheduler

class CommutesItemsList{
    var commutesItemsList = ArrayList<CommutesItemsElement>()

    init{
        val homeToSchool = CommutesItemsElement(
            "Home2School",
            "Home",
            "School",
            "01:12")
        val homeToWork = CommutesItemsElement(
            "Home2Work",
            "Home",
            "Work",
            "00:25")
        val longNameTest = CommutesItemsElement(
            "This should be long enough to fill the screen to make sure ellipsis works",
            "Wherever",
            "Wherever",
            "00:00")
        val longParamTest = CommutesItemsElement(
            "A test for parameters",
            "That one place that has a very long name",
            "That other place that has a very long name",
            "00:00")

        commutesItemsList.add(homeToWork)
        commutesItemsList.add(homeToSchool)
        commutesItemsList.add(longNameTest)
        commutesItemsList.add(longParamTest)
    }
}

data class CommutesItemsElement(
    var commutesName: String,
    var commutesStart: String,
    var commutesEnd: String,
    var commutesTime: String)