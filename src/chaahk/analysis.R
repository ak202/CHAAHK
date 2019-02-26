# TODO: Add comment
# 
# Author: akara
###############################################################################

#setwd('/media/sdd/workspace/Chaahk/r/out/')
#flist <- list.files()
#out <- do.call("rbind",lapply(flist,
#				FUN=function(files){read.table(files, header=TRUE, sep=",")}))


setwd('/media/sdd/workspace/Chaahk/output/')
out <- read.table('out_proj3.txt', header=TRUE, sep=',')
runs <- unique(out[,4])
runs <- runs[1:length(runs)]


data2 <- out[out$run==1,]
data3 <- data2[0:289,]
data4 <- data2[290:578,]
pop1 <- sum(as.numeric(data3[,1]))
pop2 <- sum(as.numeric(data4[,1]))
collapse <- pop1/pop2 
run <- data4[1,4]
data.final <- cbind(pop1, pop2, collapse, run)

for (run in runs) {
#	print(run)
	data2 <- out[out$run==run,]
#	print(as.numeric(data2[,1]))
	data3 <- data2[0:289,]
	data4 <- data2[290:578,]
	pop1 <- sum(as.numeric(data3[,1]))
	pop2 <- sum(as.numeric(data4[,1]))
	collapse <- pop1/pop2 
	run <- data4[1,4]
	data5 <- cbind(pop1, pop2, collapse, run)
	data.final <- rbind(data.final, data5)
	
}

data.final <- data.final[2:nrow(data.final),]
summary(data.final)


#setwd('/media/sdd/workspace/Chaahk/r/params/')
#flist <- list.files()
#params <- do.call("rbind",lapply(flist,
#				FUN=function(files){read.table(files, header=TRUE, sep=",")}))



params <- read.table('params_proj3.txt', header=TRUE, sep=',')
dim(params)
dim(data.final)
summary(params)
params <- params[,c(1, 9, 10, 11, 12)]
ffdata <- merge(data.final, params)
summary(ffdata)
write.csv(ffdata, 'ffdata3.csv')

plot(ffdata[,2],ffdata[,3])

dim(ffdata[ffdata$collapse>2,])



#write.csv(data.final, 'finaldata.csv')
#summary(data.final)
#hist(data.final[,3])

#summary(data.final[,2])
#splitsies <- function(data) {
#	data3 <- data[0:289,]
#	data4 <- data[290:578,]
#	pop1 <- sum(as.numeric(data3[,0]))
#	pop2 <- sum(as.numeric(data4[,0]))
#	run <- data4[1,4]
#	data5 <- cbind(pop1, pop2, dif, run)
#}
#
#
#out <- do.call("rbind",lapply(flist,
#				FUN=function(files){read.table(files, header=TRUE, sep=",")}))
#summary(out)
#
#
#
#
#
#data1 <- read.table('1out.txt', header=TRUE, sep=',')
#data2 <- data1[data1$run==1,]
#data3 <- data2[0:289,]
#data4 <- data2[290:578,]
#pop1 <- sum(as.numeric(data3[,0]))
#pop2 <- sum(as.numeric(data4[,0]))
#dif <- pop1 - pop2 
#run <- data4[1,4]
#data5 <- cbind(pop1, pop2, dif, run)




huh <- read.table('testdumb.txt', header=TRUE, sep=',')
data2 <- huh[huh$run==1,]
data3 <- data2[0:289,]
data4 <- data2[290:578,]
pop1 <- sum(as.numeric(data3[,1]))
pop2 <- sum(as.numeric(data4[,1]))
collapse <- pop1/pop2 
run <- data4[1,4]
data.final <- cbind(pop1, pop2, collapse, run)
data.final
