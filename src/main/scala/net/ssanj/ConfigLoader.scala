package net.ssanj

import java.io.File

import com.typesafe.config.{Config, ConfigException, ConfigFactory, ConfigParseOptions}


object ConfigLoader {

  type ConfigOrError = Either[ConfigLoaderError, Config]

  sealed abstract class ConfigLoaderError {
    def fold[T](h: ConfigException => T)(u: Exception => T): T
    def unify[T](u: Exception => T): T = fold(u)(u)
  }

  object ConfigLoaderError {

    def LoadConfigException(ex:ConfigException): ConfigLoaderError = new ConfigLoaderError {
      override def fold[T](h: ConfigException => T)(u: Exception => T) = h(ex)
    }

    def LoadUnknownException(ex:Exception): ConfigLoaderError = new ConfigLoaderError {
      override def fold[T](h: ConfigException => T)(u: Exception => T) = u(ex)
    }
  }

  import ConfigLoaderError._

  def loadConfigFromFile(configFile:String, f: ConfigParseOptions => ConfigParseOptions): ConfigOrError =  {
    safeConfigLoader(ConfigFactory.parseFile(new File(configFile), f(ConfigParseOptions.defaults())))
  }

  def loadConfigFromFile(configFile:String): ConfigOrError =
    loadConfigFromFile(configFile, _.setAllowMissing(false))

  def createConfigFromMap(m: Map[String, AnyRef]): ConfigOrError = {
    import scala.collection.JavaConverters.mapAsJavaMapConverter
    safeConfigLoader(ConfigFactory.parseMap(m.asJava))
  }

  private def safeConfigLoader(f: => Config): ConfigOrError = {
    try Right(f)
    catch {
      case e:ConfigException => Left(LoadConfigException(e))
      case e:Exception => Left(LoadUnknownException(e))
    }
  }
}
