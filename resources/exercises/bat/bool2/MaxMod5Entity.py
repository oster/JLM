def maxMod5(a, b):
	if (a == b):
		return 0
	elif (a > b):
		if (a % 5 == b % 5):
			return b
		else:
			return a
	else:
		if (a % 5 == b % 5):
			return a
		else:
			return b
