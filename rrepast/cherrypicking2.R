library(rrepast) 
library(ggplot2)
dir <- "/media/nvme/Chaahk/"
Easy.Setup(dir)
chaahk <- Model(modeldir=dir, dataset="population",1650, TRUE)
params.defaults <- GetSimulationParameters(chaahk)

getseed <- function(){
  return(runif(1, min = 0, max = 9999999999))
}

pview <- function(p) {
  require(htmltools)
  p <- p[ , order(colnames(p))]
  p <- t(p)
  p <- data.frame(p)
  html_print(pre(paste0(capture.output(print(p)), collapse="\n")))
}

f <- AddFactor (name = "disturbanceRemovalChance", min = 0, max = .3)
f <- AddFactor (factors = f, name = "costPromotiveRes", min = 0, max = 1)
f <- AddFactor (factors = f, name = "costPromotiveIncRate", min = 0, max = .02)
f <- AddFactor (factors = f, name = "costDemotiveRes", min = 0, max = 1)
f <- AddFactor (factors = f, name = "costDemotiveIncRate", min = 0, max = .05)
f <- AddFactor (factors = f, name = "fecundityPromotiveRes", min = 0, max = 1)
f <- AddFactor (factors = f, name = "fecundityPromotiveIncRate", min = 0, max = .01)
f <- AddFactor (factors = f, name = "fecundityDemotiveRes", min = 0, max = 1)
f <- AddFactor (factors = f, name = "fecundityDemotiveIncRate", min = 0, max = .0025)
f <- AddFactor (factors = f, name = "uplandAmount", min = 0, max = 250)
params.range <- f[,2:4]






colnames(result)
makecharts <- function(result, name) {
  cols <- c("Fecundity Promotive Level"="green","Fecundity Demotive Level"="red", "Fecundity Base"="black", "Staples" = "purple")
  lines <- c("Fecundity Promotive Level"="solid","Fecundity Demotive Level"="solid", "Fecundity Base"="dotted", "Staples" = "solid")
  sizes <- c("Fecundity Promotive Level"=1,"Fecundity Demotive Level"=1, "Fecundity Base"=.5, "Staples" = 1)
  centers <- ggplot() +
    geom_rect(aes(xmin=1000, xmax=1100, ymin=0, ymax=15), fill="red", alpha=.2) +
    geom_line(aes(x=tick, y=Fecundity.Promotive.Level, color="Fecundity Promotive Level", linetype = "Fecundity Promotive Level", size = "Fecundity Promotive Level"), data=result) +
    geom_line(aes(x=tick, y=Fecundity.Demotive.Level, color="Fecundity Demotive Level", linetype = "Fecundity Demotive Level", size = "Fecundity Demotive Level"), data=result) +
    geom_line(aes(x=tick, y=Staples, color="Staples", linetype = "Staples", size = "Staples"), data=result) +
    geom_line(aes(x=tick, y=Fecundity.Base, color = "Fecundity Base", linetype = "Fecundity Base", size = "Fecundity Base"), data=result) +
    scale_colour_manual(name = NULL, values=cols, guide = guide_legend()) +
    scale_linetype_manual(name = NULL, values=lines, guide = guide_legend()) + 
    scale_size_manual(name = NULL, values=sizes, guide = guide_legend()) +
    theme(text = element_text(size=11)) +
    labs(title = "Mean Fecundity Data of Centers", x = "Tick", y = "Value") +
    scale_x_continuous(breaks = c(35, 350, 700, 1000:1100, 1300, 1645), labels = c(0, 350, 700, rep("", 50), "Disturbance",rep("",50), 1300,1650)) +
    coord_cartesian(expand=FALSE)
  centers
  ggsave(paste("/home/akara/Dropbox/thesis/figures/analysis/", name,"FecundityChart.png", sep = ""),centers, width = 6, height = 2.25, units = "in")
  
  mx <- max(result$Cost.Demotive.Level)/result$bajoFrac[1] + 20
  mn <- min(result$Cost.Promotive.Level)/result$bajoFrac[1] -.25
  cols <- c("Cost Promotive Level"="green","Cost Demotive Level"="red", "Cost Base"="black", "Weight" = "orange")
  lines <- c("Cost Promotive Level"="solid","Cost Demotive Level"="solid", "Cost Base"="dotted", "Weight" = "solid")
  sizes <- c("Cost Promotive Level"=1,"Cost Demotive Level"=1, "Cost Base"=.5, "Weight" = 1)
  routes <- ggplot() +
    geom_rect(aes(xmin=1000, xmax=1100, ymin=mn, ymax=mx), fill="red", alpha=.2) +
    geom_line(aes(x=tick, y=Cost.Promotive.Level/bajoFrac, color="Cost Promotive Level", linetype = "Cost Promotive Level", size = "Cost Promotive Level" ), data=result) +
    geom_line(aes(x=tick, y=Cost.Demotive.Level/bajoFrac, color="Cost Demotive Level", linetype = "Cost Demotive Level", size = "Cost Demotive Level"), data=result) +
    geom_line(aes(x=tick, y=Weight/bajoFrac, color="Weight", linetype = "Weight", size = "Weight"), data=result) +
    geom_line(aes(x=tick, y=Cost.Base/bajoFrac, color = "Cost Base", linetype = "Cost Base", size = "Cost Base"), data=result) +
    scale_colour_manual(name = NULL, values=cols, guide = guide_legend()) +
    scale_linetype_manual(name = NULL, values=lines, guide = guide_legend()) + 
    scale_size_manual(name = NULL, values=sizes, guide = guide_legend()) +
    theme(text = element_text(size=11), axis.text.x = element_text(angle = 0)) +
    labs(title = "Mean Cost Data of Routes of Type 'bajo'", x = "Tick", y = "Value") +
    scale_y_continuous(trans="log", breaks = c(1,2,3,5,10,25,50), limits=c(mn,mx)) +
    scale_x_continuous(breaks = c(35, 350, 700, 1000:1100, 1300, 1645), labels = c(0, 350, 700, rep("", 50), "Disturbance",rep("",50), 1300,1650)) +
    coord_cartesian(expand=FALSE)
  routes
  ggsave(paste("/home/akara/Dropbox/thesis/figures/analysis/", name,"WeightChart.png", sep = ""),routes, width = 6, height = 2.25, units = "in")
  
  cols <- c("Labor"="yellow","Imports"="orange", "Staples" = "purple")
  sizes <- c("Labor"=1,"Imports"=1, "Staples" = 1)
  centers <- ggplot() +
    geom_rect(aes(xmin=1000, xmax=1100, ymin=0, ymax=15), fill="red", alpha=.2) +
    geom_line(aes(x=tick, y=Labor, color="Labor", size = "Labor"), data=result) +
    geom_line(aes(x=tick, y=Imports, color="Imports", size = "Imports"), data=result) +
    geom_line(aes(x=tick, y=Staples, color="Staples", size = "Staples"), data=result) +
    scale_colour_manual(name = NULL, values=cols, guide = guide_legend()) +
    scale_linetype_manual(name = NULL, values=lines, guide = guide_legend()) + 
    scale_size_manual(name = NULL, values=sizes, guide = guide_legend()) +
    theme(text = element_text(size=11)) +
    labs(title = "Mean Demographic Data of Centers", x = "Tick", y = "Value") +
    scale_x_continuous(breaks = c(35, 350, 700, 1000:1100, 1300, 1645), labels = c(0, 350, 700, rep("", 50), "Disturbance",rep("",50), 1300,1650)) +
    coord_cartesian(expand=FALSE)
  centers
  ggsave(paste("/home/akara/Dropbox/thesis/figures/analysis/", name,"DemographicsChart.png", sep = ""),centers, width = 6, height = 2.25, units = "in")
}

paramrange
makecharts(result,"default")

chaahk <- Model(modeldir=dir, dataset="population",1650, TRUE)
params <- GetSimulationParameters(chaahk)
Load(chaahk)




pview(params)
# params$disturbanceRemovalChance <- .20
# params$costDemotiveRes <- 1
# params$costPromotiveRes <- 0
# SetSimulationParameters(chaahk, params)

