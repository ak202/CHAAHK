setwd("/media/nvme/workspace2/Mayagrations/rrepast")
data <- readRDS("data.RDS")
data <- read.csv('data3.csv', row.names=1)
params <- c(1,2,3,4)

si1 <- function(i.input, i.output, df) {
	values.fixed <- unique(df[,i.input])
	mean.fixed <- function(value.fixed) {
		mean(df[df[,i.input] == value.fixed,i.output])
	}
	num <- var(sapply(values.fixed, mean.fixed))
	dem <- var(df[,i.output])
	return(num/dem)
}

r1 <- sapply(params, si1, df=data, i.output=6)
r2 <- sapply(params, si1, df=data, i.output=7)
r1
r2

si2.1 <- function(i.input.1, i.input.2, i.output, df) {
	if (i.input.1 != i.input.2) {
		values.1 <- unique(df[,i.input.1])
		values.2 <- unique(df[,i.input.2])
		round3 <- function(v1, v2) {
			return(mean(df[df[,i.input.1] == v1 & df[,i.input.2] == v2, i.output]))
		}
		round2 <- function(v) {
			result <- sapply(values.1, round3, v)
			return(result)
		}
		result <- var(unlist(lapply(values.2, round2)))
		dem <- var(df[,i.output])
		r3 <- result/dem
		r <- r3 - (r1[i.input.1] + r1[i.input.2])
		columns <- colnames(df[,c(i.input.1, i.input.2)])
		print(columns)
		print(r)
	}
	else {
		return(0)
	}
}
si2.2 <- function(i.input.1, i.input.2, i.output, df) {
	if (i.input.1 != i.input.2) {
		print('')
		i.input.3 <- i.input.2
		i.input.2 <- i.input.1
		i.input.1 <- i.input.3
		values.1 <- unique(df[,i.input.1])
		values.2 <- unique(df[,i.input.2])
		round3 <- function(v1, v2) {
			return(mean(df[df[,i.input.1] == v1 & df[,i.input.2] == v2, i.output]))
		}
		round2 <- function(v) {
			result <- sapply(values.1, round3, v)
			return(result)
		}
		result <- var(unlist(lapply(values.2, round2)))
		dem <- var(df[,i.output])
		r3 <- result/dem
		r <- r3 - (r1[i.input.1] + r1[i.input.2])
		columns <- colnames(df[,c(i.input.1, i.input.2)])
		print(columns)
		print(colnames(df)[i.output])
		print(r)
	} else {
		return(0)
	}
}

si2.3 <- function(inn, out) {
	lapply(params, si2.2, inn, out, data)
}

a <- lapply(params, si2.3, 6)
a <- lapply(params, si2.3, 7)

si2(1, 2, 5, data)

lapply(params, si2, 2, 5, data)
lapply(params, si2, 1, 5, data)


la
params




summary(data[data[,1] == 1 & data[,2] == 0,])






r1 <- sapply(params, si1, df=data, i.output=5)
r2 <- sapply(params, si1, df=data, i.output=6)




mean.fixed <- function(value.fixed.1, value.fixed.2, df) {
	mean(df[df[,i.input] == value.fixed,i.output])
}
mean.fixed <- function(value.fixed.1, value.fixed.2, df) {
	mean(df[df[,i.input.1] == value.fixed & df[,i.input.2] == value.fixed,i.output])
}

mean.fixed <- function(value.fixed.1, value.fixed.2, df) {
	mean(df[df[,i.input.1] == value.fixed.1 & df[,i.input.2] == value.fixed.2,i.output])
}

values1 <- unique(data[,1])
values2 <- unique(data[,2])



round3 <- function(v1, v2) {
	return(mean(data[data[,1] == v1 & data[,2] == v2, 5]))
}
round2 <- function(v) {
	result <- sapply(values1, round3, v)
	return(result)
}
result <- var(unlist(lapply(values2, round2)))
dem <- var(data[,5])
r3 <- result/dem
r3 - (r1[1] + r1[2])
r

concat(sapply(values2, round2))





round3(1, 2)
values2

si(1, 5, data)

0.01077267/0.1080825
var(data[,5])






