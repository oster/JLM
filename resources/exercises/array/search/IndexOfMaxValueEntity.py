def indexOfMaxValue(nums):
  max=nums[0]
  maxIdx = 0
  for i in range(len(nums)):
    if nums[i]>max:
      max = nums[i]
      maxIdx = i
  return maxIdx
