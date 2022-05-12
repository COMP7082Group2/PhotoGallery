package com.photogalleryapp.app.aspect;

import com.photogalleryapp.app.model.photo.Photo;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Aspect
public class PhotoCacheAspect {
    private HashSet<Photo> cache = null;

    @Around("execution(* com.photogalleryapp.app.model.photo.IPhotoRepository.findPhotos())")
    public Object whenFind(ProceedingJoinPoint joinPoint) throws Throwable {
        if(cache != null) {
            System.out.println("Cache hit");
            return new ArrayList<>(cache);
        }

        System.out.println("Cache miss...");

        List<Photo> r = (List<Photo>) joinPoint.proceed();

        cache = new HashSet<>(r);

        return r;
    }

    @AfterReturning(
            pointcut ="execution(* com.photogalleryapp.app.model.photo.IPhotoRepository.create( .. ))",
            returning="retVal"
    )
    public Object whenCreate(Object retVal) {
        cache.add((Photo)retVal);

        System.out.println("Added new entry to cache");

        return retVal;
    }

    @Around("execution(* com.photogalleryapp.app.model.photo.IPhotoRepository.updatePhoto( .. ))")
    public Object whenUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        Photo subject = (Photo) joinPoint.getArgs()[0];
        Photo result = (Photo) joinPoint.proceed();

        if(!result.equals(subject)) {
            cache.remove(subject);
            cache.add(result);
            System.out.println("Updated cache");
        }

        return result;
    }
}