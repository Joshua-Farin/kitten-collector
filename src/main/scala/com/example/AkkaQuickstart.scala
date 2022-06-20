package com.example

import akka.actor.typed.ActorRef
import akka.actor.typed.ActorSystem
import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import com.example.KittenProcessor.Kitten
import com.example.KittenShipper.KittenShipment
import com.example.Notifier.Notification

object Notifier {
  final case class Notification(kittens: Map[Int, Kitten])
  def apply(): Behavior[Notification] = Behaviors.receive {
    (context, message) =>
      context.log.info(message.toString())
      Behaviors.same
  }
}

object KittenShipper {
  final case class KittenShipment(
      kitten: Kitten,
      replyTo: ActorRef[Notification]
  )
  def apply(): Behavior[KittenShipment] = Behaviors.receive {
    (context, kittenShipment) =>
      context.log.info(kittenShipment.toString())

      if (kittenShipment.kitten.vaccinated == false) {
        kittenShipment.replyTo ! Notification(
          Map(kittenShipment.kitten.id -> kittenShipment.kitten)
        )
      }
      Behaviors.same
  }
}

object KittenProcessor {
  final case class Kitten(id: Int, name: String, vaccinated: Boolean)
  def apply(): Behavior[Kitten] = Behaviors.setup { context =>
    val kittenShipperRef = context.spawn(KittenShipper(), "Kitten Shipper")
    val notifierRef = context.spawn(Notifier(), "Notifier")

    Behaviors.receiveMessage { kitten =>
      context.log.info(kitten.toString())
      kittenShipperRef ! KittenShipment(
        kitten,
        notifierRef
      )
      Behaviors.same
    }
  }
}

object AkkaQuickstart extends App {
  val kittenProcessor: ActorSystem[KittenProcessor.Kitten] =
    ActorSystem(KittenProcessor(), "kittens")

  kittenProcessor ! Kitten(0, "whiskers", false)
  kittenProcessor ! Kitten(0, "tuna", true)
  kittenProcessor ! Kitten(0, "pudgy", false)
  kittenProcessor ! Kitten(0, "acorn", false)
}
