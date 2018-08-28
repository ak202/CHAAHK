data4 <- data[data$mayasim<.5,]
dim(data4)

parameters <- c("disturbanceRemovalChance", "costPromotiveRes", "costPromotiveIncRate",
                  "costDemotiveRes", "costDemotiveIncRate", "fecundityPromotiveRes",
                  "fecundityPromotiveIncRate", "fecundityDemotiveRes","fecundityDemotiveIncRate")


d <- dist(data5)
fit <- cmdscale(d,eig=TRUE, k=3)
x <- fit$points[,1]
y <- fit$points[,2]
z <- fit$points[,3]



library(cluster)
clusters <- pam(data5, k= 5, stand=TRUE)
clusplot(clusters)
clusters
summary(clusters)
colnames(data6)[10] <- "class"

data6 <- data.frame(data5, factor(clusters$clustering), x, y, z)
summary(data6)

p <- plot_ly(data6, x=~x, y=~y, z=~z, color=~class, colors=c("#BF382A", "#0C4B8E", "#16a085", "#fff933", "#6e2c00"))
add_markers(p)
summary(data6)
str(data6)

makeplots <- function(row) {
  histogram <- ggplot() + 
    geom_histogram(aes(data6[,parameters[row]]), bins=15,data=data4, fill="blue", linetype=1, color="black") +
    theme(text = element_text(size=20)) +
    labs(x = parameters[row])
    ggsave(paste("/home/akara/Dropbox/thesis/figures/rresults/",parameters[row],"Hist.png",sep=""), histogram)
}

lapply(c(1:9),makeplots)
 







