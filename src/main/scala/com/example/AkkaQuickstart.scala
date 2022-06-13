//#full-example
package com.example

import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.example.KittenProcessor.Kitten
import com.example.KittenShipper.KittenShipment
import com.example.Notifier.Notification

object Notifier {

  final case class Notification(kittenId: Int, toBeVaccinated: Boolean)

  def apply(): Behavior[Notification] = Behaviors.receive {
    (context, message) =>
      context.log.info(message.toString())

      Behaviors.same
  }

}

object KittenShipper {

  final case class KittenShipment(
      kittenId: Int,
      name: String,
      vaccinated: Boolean,
      replyTo: ActorRef[Notification]
  )

  def apply(): Behavior[KittenShipment] = Behaviors.receive {
    (context, message) =>
      context.log.info(message.toString())
      message.replyTo ! Notification(message.kittenId, true)
      Behaviors.same
  }

}

object KittenProcessor {

  final case class Kitten(id: Int, name: String, vaccinated: Boolean)

  def apply(): Behavior[Kitten] = Behaviors.setup { context =>
    val kittenShipperRef = context.spawn(KittenShipper(), "Kitten Shipper")
    val notifierRef = context.spawn(Notifier(), "Notifier")

    Behaviors.receiveMessage { message =>
      context.log.info(message.toString())
      kittenShipperRef ! KittenShipment(
        message.id,
        message.name,
        message.vaccinated,
        notifierRef
      )
      Behaviors.same
    }
  }
}

//#main-class
object AkkaQuickstart extends App {
  // #actor-system
  val kittenProcessor: ActorSystem[KittenProcessor.Kitten] =
    ActorSystem(KittenProcessor(), "kittens")
  // #actor-system

  // #main-send-messages
  kittenProcessor ! Kitten(0, "whiskers", false)
  kittenProcessor ! Kitten(0, "tuna", true)
  kittenProcessor ! Kitten(0, "pudgy", false)
  kittenProcessor ! Kitten(0, "acorn", false)
  // #main-send-messages
}
//#main-class
//#full-example
