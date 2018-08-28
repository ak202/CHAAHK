library(rpart)
library(rpart.plot)

data <- readRDS("data.RDS")

mayasim <- result$output[,2]
output <- result$dataset
parameters <- result$paramset
data <- data.frame(mayasim, output, parameters)

ctrl <- rpart.control(cp = .0025)
result2 <- rpart(mayasim ~ disturbanceRemovalChance + 
                   costPromotiveRes              +                 
                   costPromotiveIncRate          +                 
                   costDemotiveRes               +    
                   costDemotiveIncRate           +      
                   fecundityPromotiveRes         +       
                   fecundityPromotiveIncRate     +           
                   fecundityDemotiveRes          +          
                   fecundityDemotiveIncRate, data = data, control = ctrl)   
rpart.plot(result2, cex = .75)
summary(result2)

data8 <- data.frame(data, data$costPromotiveIncRate/data$costDemotiveIncRate, data$fecundityPromotiveIncRate/data$fecundityDemotiveIncRate)


colnames(data8)
plot(log(data8[,39]),data8[,1])
plot(log(data8[,38]),data8[,1])
colnames(data8)[38] <- "costratio"
colnames(data8)[39] <- "fecundityratio"
summary(data8)


awesome <- ggplot() + geom_point(aes(x=costratio, y = MaxL, color=fecundityratio), data=data8, shape=1) +
  scale_x_continuous(trans="log") +
  scale_colour_gradient(low = "yellow", high = "blue", trans = "log") +
  labs(x = "Cost Promotive Increase Rate / Cost Demotive Increase Rate", y = "Maximum Groups") +
  theme(text = element_text(size=20), panel.background = element_rect(fill="grey"))















