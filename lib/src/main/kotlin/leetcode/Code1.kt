package leetcode

object CodeMain {
    @JvmStatic
    fun main(args: Array<String>) {
//        val r = Code1.twoSum(IntArray(2), 1
        val r = Code1.longestConsecutive(intArrayOf(71791,49750,51135,36923,23702,57450,58309,49688,69946,8316))
        println(r)
    }
}

object Code1 {
    //1
    fun twoSum(nums: IntArray, target: Int): IntArray {
        val r = IntArray(2)
        for (i in nums.indices) {
            for (j in nums.indices) {
                if (i != j && nums[i] + nums[j] == target) {
                    r[0] = i
                    r[1] = j
                    return r
                }
            }
        }
        return r
    }

    //https://leetcode.cn/problems/longest-consecutive-sequence/description/?envType=study-plan-v2&envId=top-100-
    // 128
    fun longestConsecutive(nums: IntArray): Int {
        if (nums.size < 2) return nums.size
        val set = nums.toSet()
        var max = 1
        for (num in set) {
            //假设num是最长的最小值
            if (!set.contains(num - 1)) {
                var count = 1
                var baseNum = num
                while (set.contains(baseNum + 1)) {
                    count++
                    baseNum++
                    max = Math.max(max, count)
                }
            }
        }
        return max
    }
}