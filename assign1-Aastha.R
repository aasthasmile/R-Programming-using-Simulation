#Aastha Jain
#Assignment 1
#Excercise 1
craps <-function(){
  firstRoll <-TRUE
  result <-2 #intializing it with value other than 1(win) or 0(loss)
  
  repeat{
    
    if(firstRoll== TRUE){
      sum<-sample(1:6,1,replace=TRUE,1/6:1/6)+sample(1:6,1,replace=TRUE,1/6:1/6)
      
      #print(paste0("sum is ",sum))
      
      if(sum %in% c(7,11)){
        print(1)
        #print("firstroll wins")
        break
      }
      else if(sum %in% c(2,3,12)){
        print(0)
        #print("firstroll losses")
        break
      }
      
      firstRoll=FALSE
    }
    
    else if(firstRoll== FALSE){
      sum<-sample(1:6,1)+sample(1:6,1)
      #print(paste0("sum is ",sum))
      
      if(sum %in% c(7)){
        #print("secondRoll losses")
        result <-0
        print(0)
        
      }
      else if(sum %in% c(4,5,6,8,9,10)){
        #print("secondRoll wins")
        result <-1
        print(1)
      }
      
    }
    
    if(result== 1 || result==0) break
  }
}

#Excercise 2
f <-function(){
  return(sample(0:1,1))
}

estimate_bernoulli <- function(f,delta,epsilon){
  
  z <-qnorm((1+delta)/2)
  x <-vector(mode="numeric", length=10000)
  set<-vector(mode="numeric",0)
  tsum<-0
  n<-10000
  inc<-10000
  
  repeat
  {
    sum = 0
    sumsq = 0
    for (i in 1:n) {
      x[i] = f()
      sum = sum + x[i]
      sumsq = sumsq + x[i]*x[i]
    }
    tsum=tsum+sum
    
    #appending new samples to old samples
    set<-c(set,x)
    
    lambda = tsum/n
    sigmasq = (sumsq - (lambda*lambda*n))/(n-1)
    se = sqrt(var(set)/n)
    
    #print(paste0("z*se=",(z*se)))
    
    if((z*se) <= epsilon)
      {
        print(lambda)
    }
    n<-n+inc
    if(z*se <= epsilon) break
  }
 
}

#Excercise 3
#estimate_bernoulli<-function(){
  #print(paste0("The probability of winning in a games of craps is",estimate_bernoulli(craps(),0.90,0.005)));
#}


#Excercise 4
network_reliability <-function(){
  
  #prob of not breaking edge
  x=vector(mode="numeric",length=7)
  p=vector(mode="numeric",length=7)
  noofSamples<-0
  noOfBroken<-0
  lambda<-0
  Xbar=vector(mode="numeric",0)

  repeat{ 
  for(i in 1:7){
    x[i]=f()
    if(x[i]==1){
      p[i]=0.999
    }
    else if(x[i]==0){
      p[i]=0.001
    }
 }
 
  pbar<-1
  
  noofSamples<- noofSamples+1
  #print(paste0("noofSamples:",noofSamples))
  
  #conditions for which network is broken
  if(x[1]==0 && x[2]==0 ||
     x[5]==0 && x[6]==0 && x[7]==0 || 
     x[1]==0 && x[4]==0 && x[7]==0 ||
     x[2]==0 && x[3]==0 && x[5]==0 ||
     x[2]==0 && x[4]==0 && x[5]==0 && x[6]==0 ||
     x[1]==0 && x[3]==0 && x[6]==0 && x[7]==0 ||
     x[3]==0 && x[4]==0 && x[5]==0 && x[7]==0  ){
    
    noOfBroken <- noOfBroken +1
    
    Xbar[noofSamples]=1
    
    for(i in 1:length(p)){
      pbar <- pbar*p[i]
    }
    lambda <-lambda +pbar
  } else{
    Xbar[noofSamples]=0
  }
  
  
  if(noOfBroken == 50) break
  }
  #print(Xbar)
  print(lambda)
  
  #variance is E[X^2]-E^2[X]
  variance=(50*lambda -(lambda^2))
  print(variance)
  
  #number of samples n needed to reach 50 broken configurations
  print(noofSamples)
}

#Excercise 5
network_reliability2<-function(){
  
  #prob of not breaking edge
  x=vector(mode="numeric",length=7)
  p=vector(mode="numeric",length=7)
  noofSamples<-0
  noOfBroken<-0
  lambda<-0
  Xbar=vector(mode="numeric",0)
  m<-7
  r<-2
  val<-((0.999/0.714)^(m-r) ) * ((0.001/0.285)^r)
  
  repeat{ 
    for(i in 1:7){
      x[i]=f()
      if(x[i]==1){
        p[i]=(1-(r/m)) #change of Be(q) from Be(0.999) and q=(1-r/m)
      }
      else if(x[i]==0){
        p[i]=(r/m)
      }
    }
    
    pbar<-1
    
    noofSamples<- noofSamples+1
    #print(paste0("noofSamples:",noofSamples))
    
    #conditions for which network is broken
    if(x[1]==0 && x[2]==0 ||
       x[5]==0 && x[6]==0 && x[7]==0 || 
       x[1]==0 && x[4]==0 && x[7]==0 ||
       x[2]==0 && x[3]==0 && x[5]==0 ||
       x[2]==0 && x[4]==0 && x[5]==0 && x[6]==0 ||
       x[1]==0 && x[3]==0 && x[6]==0 && x[7]==0 ||
       x[3]==0 && x[4]==0 && x[5]==0 && x[7]==0  ){
      
      noOfBroken <- noOfBroken +1
      
      #applying X = 1 is replaced with ??(x,0.999)/??(x,5/7)
      #where ??(x,0.999)/??(x,5/7) =(p/q)^m-r * (1-p/1-q)^r
      Xbar[noofSamples]=((0.999/0.714)^(m-r) ) * ((0.001/0.285)^r)
      
      for(i in 1:length(p)){
        pbar <- pbar*p[i]
      }
      lambda <-lambda + val*pbar
    }
    else{
      Xbar[noofSamples]=0
    }
    if(noOfBroken == 50) break
  }
  xbar_summation<- 1
  for(i in 1:length(p)){
    if(Xbar[i]!=0){    xbar_summation <- xbar_summation*Xbar[i]}
  }
  #print(Xbar)
  #print(xbar_summation)
  print(lambda)
  
  #variance is e[x^2]-E^2[X]
  variance=(50*(val^2) - noofSamples*(lambda^2))/(noofSamples-1)
  print(variance)
  
  #number of samples n needed to reach 50 broken configurations
  print(noofSamples) 
}