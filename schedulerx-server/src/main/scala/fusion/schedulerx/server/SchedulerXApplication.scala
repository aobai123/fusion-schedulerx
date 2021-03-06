/*
 * Copyright 2019 akka-fusion.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fusion.schedulerx.server

import akka.{ actor => classic }
import akka.actor.typed.scaladsl.adapter._
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import com.typesafe.config.ConfigFactory
import fusion.schedulerx.SchedulerX
import fusion.schedulerx.server.route.Routes

object SchedulerXApplication {
  def main(args: Array[String]): Unit = {
    val schedulerX = SchedulerX.fromOriginalConfig(ConfigFactory.load())
    startHttp(schedulerX)(schedulerX.system.toClassic)
  }

  private def startHttp(schedulerX: SchedulerX)(implicit system: classic.ActorSystem): Unit = {
    val schedulerXBroker = SchedulerXServer(schedulerX)
    val route: Route = new Routes(schedulerXBroker).route
    val config = schedulerX.config
    Http().bindAndHandle(
      route,
      config.getString("fusion.http.default.server.host"),
      config.getInt("fusion.http.default.server.port"))
  }
}
